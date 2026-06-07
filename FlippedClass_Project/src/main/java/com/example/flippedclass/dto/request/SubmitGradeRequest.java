package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitGradeRequest {

    @NotNull(message = "sessionId is required")
    @Positive(message = "sessionId must be a positive number")
    private Long sessionId;

    @NotNull(message = "studentId is required")
    @Positive(message = "studentId must be a positive number")
    private Long studentId;

    @NotNull(message = "criterionId is required")
    @Positive(message = "criterionId must be a positive number")
    private Long criterionId;

    @NotNull(message = "lecturerId is required")
    @Positive(message = "lecturerId must be a positive number")
    private Long lecturerId;

    @NotNull(message = "score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "score must be greater than or equal to 0")
    private BigDecimal score;

    @NotBlank(message = "comment is required")
    @Size(min = 10, max = 1000, message = "comment must be between 10 and 1000 characters")
    private String comment;
}
