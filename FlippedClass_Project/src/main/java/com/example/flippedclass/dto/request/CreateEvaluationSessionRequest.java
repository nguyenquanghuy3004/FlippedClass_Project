package com.example.flippedclass.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEvaluationSessionRequest {

    @NotNull(message = "learningPathId is required")
    @Positive(message = "learningPathId must be a positive number")
    private Long learningPathId;

    @NotNull(message = "lecturerId is required")
    @Positive(message = "lecturerId must be a positive number")
    private Long lecturerId;

    @NotBlank(message = "title is required")
    @Size(min = 3, max = 255, message = "title must be between 3 and 255 characters")
    private String title;

    private LocalDateTime gradingStartAt;

    private LocalDateTime gradingDeadlineAt;

    @NotEmpty(message = "at least one evaluation criterion is required")
    @Valid
    private List<CriterionItem> criteria;

    @AssertTrue(message = "gradingDeadlineAt must be after gradingStartAt")
    public boolean isGradingPeriodValid() {
        if (gradingStartAt == null || gradingDeadlineAt == null) {
            return true;
        }
        return gradingDeadlineAt.isAfter(gradingStartAt);
    }

    @Data
    public static class CriterionItem {

        @NotBlank(message = "criterion name is required")
        @Size(min = 2, max = 255, message = "criterion name must be between 2 and 255 characters")
        private String name;

        @Size(max = 500, message = "criterion description must not exceed 500 characters")
        private String description;

        @NotNull(message = "criterion maxScore is required")
        @DecimalMin(value = "0.01", message = "criterion maxScore must be greater than 0")
        private BigDecimal maxScore;

        @jakarta.validation.constraints.Min(value = 0, message = "criterion sortOrder must be at least 0")
        @jakarta.validation.constraints.Max(value = 999, message = "criterion sortOrder must not exceed 999")
        private Integer sortOrder;
    }
}
