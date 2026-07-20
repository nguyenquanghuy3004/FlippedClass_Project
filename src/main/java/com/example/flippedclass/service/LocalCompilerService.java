package com.example.flippedclass.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LocalCompilerService {

    // Hàm thực thi mã Java trực tiếp trên server, hỗ trợ cả việc truyền input đầu vào (stdin)
    // Trả về output của chương trình (bao gồm cả lỗi compile nếu có)
    public String executeJavaCode(String code, String stdinInput) {
        // Sinh một mã ngẫu nhiên UUID để tạo thư mục ảo độc lập cho mỗi lần chạy code
        String executionId = UUID.randomUUID().toString();
        Path tempDir = Paths.get("temp_exec", executionId);
        
        try {
            // 1. Tạo thư mục ảo riêng biệt cho từng request để chống đụng độ (Race condition) giữa nhiều học sinh
            Files.createDirectories(tempDir);
            
            // 2. Tạo file Main.java trong thư mục ảo và ghi toàn bộ code học sinh nộp vào đó
            Path sourceFile = tempDir.resolve("Main.java");
            Files.writeString(sourceFile, code, StandardCharsets.UTF_8);
            
            // 3. Khởi tạo tiến trình biên dịch code bằng lệnh 'javac Main.java'
            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(tempDir.toFile());
            // Gộp cả luồng báo lỗi (ErrorStream) vào luồng chuẩn (InputStream) để dễ lấy dữ liệu log
            compilePb.redirectErrorStream(true);
            Process compileProcess = compilePb.start();
            
            // Đọc kết quả log trả về từ trình biên dịch javac
            String compileOutput = readProcessOutput(compileProcess);
            // Giới hạn thời gian biên dịch tối đa là 5 giây để chống treo server
            boolean isCompiled = compileProcess.waitFor(5, TimeUnit.SECONDS);
            
            // Nếu quá 5 giây mà chưa compile xong -> Ép chết tiến trình (Kill process)
            if (!isCompiled) {
                compileProcess.destroyForcibly();
                return "Compile Time Limit Exceeded\n" + compileOutput;
            }
            // Nếu exitValue != 0 có nghĩa là code bị lỗi cú pháp (Syntax error)
            if (compileProcess.exitValue() != 0) {
                return "Compile Error:\n" + compileOutput;
            }

            // 4. Nếu biên dịch thành công, khởi tạo tiến trình chạy code bằng lệnh 'java Main'
            ProcessBuilder runPb = new ProcessBuilder("java", "Main");
            runPb.directory(tempDir.toFile());
            runPb.redirectErrorStream(true);
            Process runProcess = runPb.start();

            // Nếu bài tập có yêu cầu nhập dữ liệu đầu vào (Ví dụ: dùng Scanner), tiến hành nhồi stdin vào luồng chạy
            if (stdinInput != null && !stdinInput.isEmpty()) {
                try (OutputStream os = runProcess.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(stdinInput);
                    // Gửi ký tự ngắt dòng, tương đương thao tác ấn phím Enter trên bàn phím
                    writer.newLine(); 
                    // Đẩy toàn bộ dữ liệu trong bộ đệm vào luồng
                    writer.flush();
                }
            }

            // Chuẩn bị biến lưu kết quả đầu ra
            StringBuilder outputBuilder = new StringBuilder();
            
            // Chạy riêng một luồng (Thread) phụ chuyên làm nhiệm vụ đọc kết quả in ra màn hình (System.out.print)
            Thread readThread = new Thread(() -> {
                outputBuilder.append(readProcessOutput(runProcess));
            });
            readThread.start();

            // Thiết lập giới hạn thời gian chạy code tối đa là 3 giây để chống lỗi lặp vô hạn (Infinite Loop)
            boolean isFinished = runProcess.waitFor(3, TimeUnit.SECONDS);
            
            // Nếu quá 3 giây mà code vẫn đang chạy -> Kill tiến trình Java và dừng luồng đọc kết quả
            if (!isFinished) {
                runProcess.destroyForcibly();
                readThread.interrupt();
                return "Time Limit Exceeded (Quá 3 giây - Có thể do lặp vô hạn)";
            }

            // Chờ tối đa thêm 1 giây để luồng đọc (readThread) lấy nốt những dòng dữ liệu cuối cùng trước khi đóng
            readThread.join(1000); 
            return outputBuilder.toString();

        } catch (Exception e) {
            // Bắt mọi ngoại lệ có thể xảy ra ở Server (như lỗi hết dung lượng đĩa, lỗi cấp quyền chạy lệnh bash)
            return "Server Error: " + e.getMessage();
        } finally {
            // 5. Cleanup: Dù chạy thành công hay thất bại, bắt buộc phải xóa sạch thư mục ảo để giải phóng ổ cứng
            deleteDirectoryRecursively(tempDir.toFile());
        }
    }

    // Hàm phụ trợ dùng để đọc luồng dữ liệu (Stream) trả về từ tiến trình (Process) của hệ điều hành
    private String readProcessOutput(Process process) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            // Đọc từng dòng log cho đến khi kết thúc chương trình
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            // Bỏ qua lỗi trong quá trình đọc stream (thường xảy ra khi process bị kill đột ngột)
        }
        return output.toString();
    }

    // Hàm đệ quy dùng để xóa toàn bộ một thư mục và tất cả các file con bên trong nó
    private void deleteDirectoryRecursively(File file) {
        // Nếu file truyền vào null hoặc không tồn tại thì dừng đệ quy
        if (file == null || !file.exists()) return;
        
        // Nếu là thư mục, phải quét tất cả các file con bên trong và gọi đệ quy xóa chúng trước
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectoryRecursively(child);
                }
            }
        }
        // Sau khi các file con đã sạch sẽ, hoặc bản thân nó là file, tiến hành xóa chính nó
        file.delete();
    }
}
