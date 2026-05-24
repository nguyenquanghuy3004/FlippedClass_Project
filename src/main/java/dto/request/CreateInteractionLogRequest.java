package dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateInteractionLogRequest {

    @NotNull(message = "studentId is required")
    @Positive(message = "studentId must be a positive number")
    private Long studentId;

    @NotNull(message = "learningPathId is required")
    @Positive(message = "learningPathId must be a positive number")
    private Long learningPathId;

    @Size(max = 50, message = "interactionType must not exceed 50 characters")
    private String interactionType;

    @Size(min = 5, max = 2000, message = "summary must be between 5 and 2000 characters")
    private String summary;

    @PastOrPresent(message = "occurredAt must not be in the future")
    private LocalDateTime occurredAt;
}
