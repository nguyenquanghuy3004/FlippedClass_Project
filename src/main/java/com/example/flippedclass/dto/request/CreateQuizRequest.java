package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQuizRequest {

    @Positive(message = "learningNodeId must be a positive number")
    private Long learningNodeId;

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

    @Min(value = 1, message = "passScore must be at least 1")
    @Max(value = 100, message = "passScore must not exceed 100")
    private Integer passScore;

    @Size(max = 20, message = "difficulty must not exceed 20 characters")
    private String difficulty;

    @Size(max = 1000, message = "thumbnailUrl must not exceed 1000 characters")
    private String thumbnailUrl;
}
