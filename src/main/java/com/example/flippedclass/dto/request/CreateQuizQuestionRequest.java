package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQuizQuestionRequest {

    @NotBlank(message = "content is required")
    @Size(min = 1, max = 1000, message = "content must be between 1 and 1000 characters")
    private String content;

    @Size(max = 4000, message = "options must not exceed 4000 characters")
    private String options;

    @NotBlank(message = "correctAnswer is required")
    @Size(min = 1, max = 500, message = "correctAnswer must be between 1 and 500 characters")
    private String correctAnswer;

    @NotNull(message = "points is required")
    @Min(value = 1, message = "points must be at least 1")
    @Max(value = 100, message = "points must not exceed 100")
    private Integer points;

    @Size(max = 50, message = "questionType must not exceed 50 characters")
    private String questionType;

    private Integer sortOrder;
}
