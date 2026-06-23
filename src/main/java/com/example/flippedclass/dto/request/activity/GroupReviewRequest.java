package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupReviewRequest {

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be >= 0")
    @Max(value = 10, message = "Score must be <= 10")
    private Double score;

    private String feedback;
}
