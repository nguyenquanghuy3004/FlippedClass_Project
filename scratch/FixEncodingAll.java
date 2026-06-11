import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FixEncodingAll {
    public static void main(String[] args) throws Exception {
        Path startPath = Paths.get("d:/Documents/HSF/FlippedClass_Project/src");
        
        try (Stream<Path> stream = Files.walk(startPath)) {
            stream.filter(Files::isRegularFile)
                  .filter(p -> p.toString().endsWith(".html") || p.toString().endsWith(".java") || p.toString().endsWith(".js") || p.toString().endsWith(".css"))
                  .forEach(path -> {
                      try {
                          String content = Files.readString(path, StandardCharsets.UTF_8);
                          byte[] bytes = new byte[content.length()];
                          boolean needsFix = false;
                          for (int i = 0; i < content.length(); i++) {
                              char c = content.charAt(i);
                              bytes[i] = (byte) c;
                              if (c == 0xC3 || c == 0xE1 || c == 0xC4 || c == 0xC5) {
                                  needsFix = true;
                              }
                          }
                          
                          if (needsFix) {
                              String fixed = new String(bytes, StandardCharsets.UTF_8);
                              // We use a broad range of Vietnamese characters as heuristic
                              if (fixed.contains("Không") || fixed.contains("Tổng") || fixed.contains("Sinh") || 
                                  fixed.contains("Đăng") || fixed.contains("đại diện") || fixed.contains("được") ||
                                  fixed.contains("thêm") || fixed.contains("người") || fixed.contains("hệ thống") ||
                                  fixed.contains("Đánh giá") || fixed.contains("Bắt đầu") || fixed.contains("Tên") ||
                                  fixed.contains("Mật khẩu") || fixed.contains("Học sinh") || fixed.contains("Giảng viên") ||
                                  fixed.contains("Lớp") || fixed.contains("Hủy") || fixed.contains("Lưu") ||
                                  fixed.contains("Xác nhận") || fixed.contains("Câu hỏi") || fixed.contains("Tạo") ||
                                  fixed.contains("Sửa") || fixed.contains("Xóa") || fixed.contains("Đóng") ||
                                  fixed.contains("đã") || fixed.contains("đang") || fixed.contains("sẽ") ||
                                  fixed.contains("các") || fixed.contains("và") || fixed.contains("có") ||
                                  fixed.contains("của") || fixed.contains("để") || fixed.contains("cho") ||
                                  fixed.contains("với") || fixed.contains("trong") || fixed.contains("kết quả")) {
                                  
                                  Files.writeString(path, fixed, StandardCharsets.UTF_8);
                                  System.out.println("Fixed: " + path.toString());
                              }
                          }
                      } catch (Exception e) {
                          // Ignore read errors
                      }
                  });
        }
    }
}
