package dto.response;

import lombok.Builder;
import lombok.Data;
import enums.SummaryStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class LessonSummaryResponse {
    private Long id;
    private Long learningNodeId;
    private Long studentId;
    private String studentName;
    private String summaryContent;
    private String lecturerFeedback;
    private SummaryStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
