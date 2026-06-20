package com.example.flippedclass.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LocalCompilerService {

    // Trả về output của chương trình (bao gồm cả lỗi compile nếu có)
    public String executeJavaCode(String code, String stdinInput) {
        String executionId = UUID.randomUUID().toString();
        Path tempDir = Paths.get("temp_exec", executionId);
        
        try {
            // 1. Tạo thư mục ảo riêng biệt
            Files.createDirectories(tempDir);
            
            // 2. Ghi code vào Main.java
            Path sourceFile = tempDir.resolve("Main.java");
            Files.writeString(sourceFile, code, StandardCharsets.UTF_8);
            
            // 3. Biên dịch code (javac)
            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true);
            Process compileProcess = compilePb.start();
            
            String compileOutput = readProcessOutput(compileProcess);
            boolean isCompiled = compileProcess.waitFor(5, TimeUnit.SECONDS);
            
            if (!isCompiled) {
                compileProcess.destroyForcibly();
                return "Compile Time Limit Exceeded\n" + compileOutput;
            }
            if (compileProcess.exitValue() != 0) {
                return "Compile Error:\n" + compileOutput;
            }

            // 4. Chạy code (java)
            ProcessBuilder runPb = new ProcessBuilder("java", "Main");
            runPb.directory(tempDir.toFile());
            runPb.redirectErrorStream(true);
            Process runProcess = runPb.start();

            // Nhồi stdin vào nếu có
            if (stdinInput != null && !stdinInput.isEmpty()) {
                try (OutputStream os = runProcess.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(stdinInput);
                    writer.newLine(); // Đảm bảo ấn Enter
                    writer.flush();
                }
            }

            // Đọc kết quả với Timeout 3 giây
            StringBuilder outputBuilder = new StringBuilder();
            Thread readThread = new Thread(() -> {
                outputBuilder.append(readProcessOutput(runProcess));
            });
            readThread.start();

            boolean isFinished = runProcess.waitFor(3, TimeUnit.SECONDS);
            if (!isFinished) {
                runProcess.destroyForcibly();
                readThread.interrupt();
                return "Time Limit Exceeded (Quá 3 giây - Có thể do lặp vô hạn)";
            }

            readThread.join(1000); // Đợi thread đọc nốt dữ liệu
            return outputBuilder.toString();

        } catch (Exception e) {
            return "Server Error: " + e.getMessage();
        } finally {
            // 5. Cleanup: Xóa sạch thư mục ảo
            deleteDirectoryRecursively(tempDir.toFile());
        }
    }

    private String readProcessOutput(Process process) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            // Bỏ qua lỗi đọc stream
        }
        return output.toString();
    }

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
