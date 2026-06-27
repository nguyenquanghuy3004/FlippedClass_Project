package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LecturerGradeRequest {
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be less than 0")
    @Max(value = 10, message = "Score cannot be greater than 10")
    private Double score;

    private String feedback;
}
