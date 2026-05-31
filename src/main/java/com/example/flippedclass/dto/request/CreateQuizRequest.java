package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQuizRequest {

    @NotNull(message = "learningNodeId is required")
    @Positive(message = "learningNodeId must be a positive number")
    private Long learningNodeId;

    @NotNull(message = "lecturerId is required")
    @Positive(message = "lecturerId must be a positive number")
    private Long lecturerId;

    @NotBlank(message = "title is required")
    @Size(min = 3, max = 255, message = "title must be between 3 and 255 characters")
    private String title;

    @Size(max = 500, message = "description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "durationMinutes must be at least 1")
    @Max(value = 480, message = "durationMinutes must not exceed 480")
    private Integer durationMinutes;

    private Boolean active;
}
