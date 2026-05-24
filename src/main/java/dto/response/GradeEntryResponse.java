package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GradeEntryResponse {
    private Long id;
    private Long sessionId;
    private Long studentId;
    private String studentName;
    private Long criterionId;
    private String criterionName;
    private BigDecimal score;
    private String comment;
    private LocalDateTime gradedAt;
}
