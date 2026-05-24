package dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizQuestionResponse {
    private Long id;
    private Long quizId;
    private String content;
    private String options;
    private String correctAnswer;
    private Integer points;
}
