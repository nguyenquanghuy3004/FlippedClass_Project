package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubmitSummaryRequest {
    @NotNull(message = "learningNodeId is required")
    @Positive(message = "learningNodeId must be a positive number")
    private Long learningNodeId;

    @NotNull(message = "studentId is required")
    @Positive(message = "studentId must be a positive number")
    private Long studentId;

    @NotBlank(message = "summaryContent is required")
    private String summaryContent;
}
