package dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQuizQuestionRequest {

    @NotBlank(message = "content is required")
    @Size(min = 5, max = 500, message = "content must be between 5 and 500 characters")
    private String content;

    @Size(max = 1000, message = "options must not exceed 1000 characters")
    private String options;

    @NotBlank(message = "correctAnswer is required")
    @Size(min = 1, max = 10, message = "correctAnswer must be between 1 and 10 characters")
    private String correctAnswer;

    @NotNull(message = "points is required")
    @Min(value = 1, message = "points must be at least 1")
    @Max(value = 100, message = "points must not exceed 100")
    private Integer points;
}
