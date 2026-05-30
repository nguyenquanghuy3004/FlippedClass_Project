package dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackSummaryRequest {
    @NotBlank(message = "lecturerFeedback is required")
    private String lecturerFeedback;
}
