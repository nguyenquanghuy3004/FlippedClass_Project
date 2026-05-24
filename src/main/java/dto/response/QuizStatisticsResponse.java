package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class QuizStatisticsResponse {
    private Long quizId;
    private String quizTitle;
    private long totalAttempts;
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;
}
