package com.example.flippedclass.service;

import com.example.flippedclass.util.VideoUrlNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/videos/";

    @Value("${flippedclass.app.baseUrl:http://localhost:8080}")
    private String baseUrl;

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File video không được để trống");
        }

        try {
            Path uploadPath = Path.of(UPLOAD_DIR).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String originalFileName = file.getOriginalFilename();
                         String extension = ".mp4";
                if (originalFileName != null && originalFileName.contains(".")) {
                    extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            }

            String newFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/videos/" + newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi lưu file video", ex);
        }
    }

    public String toFullUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        String path = url.toLowerCase().contains("/uploads/pdfs/")
                ? url.substring(url.toLowerCase().indexOf("/uploads/pdfs/"))
                : VideoUrlNormalizer.normalize(url);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return VideoUrlNormalizer.toFullUrl(baseUrl, path);
    }

    public String storePdf(MultipartFile file){
        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File video không được để trống");
        }
            try{
                Path uploadPath = Path.of("uploads/pdfs").toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String originalFileName = file.getOriginalFilename();
                 String extension = ".pdf";
                if (originalFileName != null && originalFileName.contains(".")){
                    extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                }

                        String newFileName = UUID.randomUUID() + extension;
                        Path filePath = uploadPath.resolve(newFileName);
                        Files.copy(file.getInputStream(), filePath);

                return "/uploads/pdfs/" + newFileName;
            } catch (IOException ex){

                throw new RuntimeException("Lỗi khi lưu file tài liệu PDF", ex);
            }

            }
}
