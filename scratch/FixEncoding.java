import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class FixEncoding {
    public static void main(String[] args) throws Exception {
        String[] files = {
            "d:/Documents/HSF/FlippedClass_Project/src/main/resources/templates/student/learning-spaces.html",
            "d:/Documents/HSF/FlippedClass_Project/src/main/resources/templates/student/student-dashboard.html",
            "d:/Documents/HSF/FlippedClass_Project/src/main/resources/templates/student/my-quizzes.html",
            "d:/Documents/HSF/FlippedClass_Project/src/main/resources/templates/student/quiz-statistics.html",
            "d:/Documents/HSF/FlippedClass_Project/src/main/resources/templates/student/take-quiz.html"
        };
        for (String file : files) {
            Path path = Paths.get(file);
            if (!Files.exists(path)) continue;
            
            String content = Files.readString(path, StandardCharsets.UTF_8);
            byte[] bytes = new byte[content.length()];
            boolean needsFix = false;
            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                bytes[i] = (byte) c;
                if (c == 0xC3 || c == 0xE1) { // Ã or á
                    needsFix = true;
                }
            }
            if (needsFix) {
                String fixed = new String(bytes, StandardCharsets.UTF_8);
                // Extra safety check: verify it actually fixed something known
                if (fixed.contains("Không") || fixed.contains("Tổng") || fixed.contains("Sinh")) {
                    Files.writeString(path, fixed, StandardCharsets.UTF_8);
                    System.out.println("Fixed " + file);
                } else {
                    System.out.println("Could not confidently fix " + file);
                }
            }
        }
    }
}
