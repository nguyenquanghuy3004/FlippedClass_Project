import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FixEncodingAll2 {
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
                              if (c >= 0xC0 && c <= 0xC3) { // common leading bytes for double-encoded UTF-8 in ISO-8859-1
                                  needsFix = true;
                              }
                          }
                          
                          if (needsFix) {
                              String fixed = new String(bytes, StandardCharsets.UTF_8);
                              // Heuristic: if decoding it doesn't create replacement characters,
                              // and the new string is actually different from the old string.
                              // Also, check if it contains common Vietnamese chars after decoding:
                              boolean hasVietnamese = fixed.matches(".*[àáãạảăắằẵặẳâấầẫậẩđèéẹẻẽêếềễệểìíĩỉịòóõọỏôốồỗộổơớờỡợởùúũụủưứừữựửỳýỹỷỵ].*");
                              if (!fixed.contains("\uFFFD") && hasVietnamese) {
                                  Files.writeString(path, fixed, StandardCharsets.UTF_8);
                                  System.out.println("Fixed via broad heuristic: " + path.toString());
                              }
                          }
                      } catch (Exception e) {
                      }
                  });
        }
    }
}
