package com.example.flippedclass.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LocalCompilerService {

    // Thực thi mã Java trên server, trả về kết quả hoặc lỗi
    public String executeJavaCode(String code, String stdinInput) {
        // Tạo thư mục ảo riêng cho từng request để tránh ghi đè code (Race condition)
        String executionId = UUID.randomUUID().toString();
        Path tempDir = Paths.get("temp_exec", executionId);
        
        try {
            Files.createDirectories(tempDir);
            
            // 1. Tạo file Main.java chứa code của học sinh
            Path sourceFile = tempDir.resolve("Main.java");
            Files.writeString(sourceFile, code, StandardCharsets.UTF_8);
            
            // 2. Biên dịch code (javac)
            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true); // Gộp lỗi vào chung kết quả trả về
            Process compileProcess = compilePb.start();
            
            String compileOutput = readProcessOutput(compileProcess);
            // Giới hạn thời gian compile là 5 giây
            boolean isCompiled = compileProcess.waitFor(5, TimeUnit.SECONDS);
            
            if (!isCompiled) {
                compileProcess.destroyForcibly(); // Ép dừng nếu quá giờ
                return "Compile Time Limit Exceeded\n" + compileOutput;
            }
            if (compileProcess.exitValue() != 0) {
                return "Compile Error:\n" + compileOutput; // Báo lỗi cú pháp
            }

            // 3. Thực thi code (java Main)
            ProcessBuilder runPb = new ProcessBuilder("java", "Main");
            runPb.directory(tempDir.toFile());
            runPb.redirectErrorStream(true);
            Process runProcess = runPb.start();

            // Nếu có Input (stdin), truyền dữ liệu vào luồng chạy của chương trình
            if (stdinInput != null && !stdinInput.isEmpty()) {
                try (OutputStream os = runProcess.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(stdinInput);
                    writer.newLine(); 
                    writer.flush();
                }
            }

            StringBuilder outputBuilder = new StringBuilder();
            
            // Chạy luồng phụ để đọc kết quả in ra màn hình
            Thread readThread = new Thread(() -> {
                outputBuilder.append(readProcessOutput(runProcess));
            });
            readThread.start();

            // Giới hạn chạy tối đa 3 giây để chống lặp vô hạn (Infinite Loop)
            boolean isFinished = runProcess.waitFor(5, TimeUnit.SECONDS);
            
            if (!isFinished) {
                runProcess.destroyForcibly();
                readThread.interrupt();
                return "Time Limit Exceeded (Quá 5 giây - Có thể do lặp vô hạn)";
            }

            readThread.join(1000); 
            return outputBuilder.toString();

        } catch (Exception e) {
            return "Server Error: " + e.getMessage();
        } finally {
            // 4. Dọn dẹp: Xóa sạch thư mục ảo để giải phóng bộ nhớ
            deleteDirectoryRecursively(tempDir.toFile());
        }
    }

    // Đọc kết quả văn bản từ tiến trình
    private String readProcessOutput(Process process) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {}
        return output.toString();
    }

    // Xóa thư mục và các file con bên trong
    private void deleteDirectoryRecursively(File file) {
        if (file == null || !file.exists()) return;
        
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectoryRecursively(child);
                }
            }
        }
        file.delete();
    }
}
