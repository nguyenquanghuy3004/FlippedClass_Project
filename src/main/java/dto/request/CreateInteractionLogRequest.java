package dto.request;

import entity.enums.InteractionType;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "courseName is required")
    @Size(min = 2, max = 100, message = "courseName must be between 2 and 100 characters")
    private String courseName;

    @NotNull(message = "type is required")
    private InteractionType type;

    @NotBlank(message = "summary is required")
    @Size(min = 5, max = 500, message = "summary must be between 5 and 500 characters")
    private String summary;

    @PastOrPresent(message = "occurredAt must not be in the future")
    private LocalDateTime occurredAt;
}
