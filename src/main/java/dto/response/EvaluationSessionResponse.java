package dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EvaluationSessionResponse {
    private Long id;
    private String title;
    private String courseName;
    private Long lecturerId;
    private String lecturerName;
    private LocalDateTime gradingStartAt;
    private LocalDateTime gradingDeadlineAt;
    private List<CriterionResponse> criteria;
}
