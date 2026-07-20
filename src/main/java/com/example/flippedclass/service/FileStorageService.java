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

    // Thư mục lưu trữ video mặc định
    private static final String UPLOAD_DIR = "uploads/videos/";

    // Domain gốc của ứng dụng (lấy từ application.properties), mặc định là localhost:8080
    @Value("${flippedclass.app.baseUrl:http://localhost:8080}")
    private String baseUrl;

    /**
     * Nhận file Video từ client, đổi tên file bằng UUID để chống trùng lặp, và lưu vào ổ cứng.
     * 
     * @param file File video upload lên (MultipartFile)
     * @return Đường dẫn tương đối của file sau khi lưu (VD: /uploads/videos/abc-123.mp4)
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File video không được để trống");
        }

        try {
            // Xác định thư mục lưu trữ tuyệt đối và tạo thư mục nếu chưa tồn tại
            Path uploadPath = Path.of(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Lấy tên gốc và định dạng đuôi file (Mặc định là .mp4)
            String originalFileName = file.getOriginalFilename();
            String extension = ".mp4";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            }

            // Sinh tên mới ngẫu nhiên bằng mã UUID để không bị đè file khi trùng tên
            String newFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            
            // Copy luồng byte của file vào ổ cứng
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/videos/" + newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi lưu file video", ex);
        }
    }

    /**
     * Hàm tiện ích giúp nối chuỗi đường dẫn tương đối (từ DB) thành URL tuyệt đối hoàn chỉnh.
     * Ví dụ: "/uploads/pdfs/abc.pdf" -> "http://localhost:8080/uploads/pdfs/abc.pdf"
     * 
     * @param url Đường dẫn tương đối
     * @return URL truy cập tuyệt đối
     */
    public String toFullUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        
        // Chuẩn hóa đường dẫn: nếu là PDF thì giữ nguyên thư mục gốc, nếu là Video thì gọi hàm Normalizer
        String path = url.toLowerCase().contains("/uploads/pdfs/")
                ? url.substring(url.toLowerCase().indexOf("/uploads/pdfs/"))
                : VideoUrlNormalizer.normalize(url);
                
        // Đảm bảo đường dẫn luôn bắt đầu bằng dấu "/"
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        // Nối với Base URL
        return VideoUrlNormalizer.toFullUrl(baseUrl, path);
    }

    /**
     * Nhận file tài liệu PDF từ client, đổi tên bằng UUID và lưu vào ổ cứng.
     * Hoạt động tương tự như hàm storeFile bên trên nhưng lưu vào thư mục 'uploads/pdfs'.
     * 
     * @param file File PDF upload lên (MultipartFile)
     * @return Đường dẫn tương đối của file sau khi lưu (VD: /uploads/pdfs/xyz-789.pdf)
     */
    public String storePdf(MultipartFile file){
        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        try{
            // Xác định thư mục uploads/pdfs tuyệt đối và khởi tạo nếu chưa có
            Path uploadPath = Path.of("uploads/pdfs").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Xử lý lấy đuôi file (mặc định .pdf)
            String originalFileName = file.getOriginalFilename();
            String extension = ".pdf";
            if (originalFileName != null && originalFileName.contains(".")){
                extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            }

            // Sinh tên mới và lưu file
            String newFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/pdfs/" + newFileName;
        } catch (IOException ex){
            throw new RuntimeException("Lỗi khi lưu file tài liệu PDF", ex);
        }
    }
}
