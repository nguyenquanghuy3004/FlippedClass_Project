package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizAttemptResponse {
    private Long id;
    private Long quizId;
    private Long studentId;
    private String studentName;
    private BigDecimal score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private LocalDateTime submittedAt;
}
