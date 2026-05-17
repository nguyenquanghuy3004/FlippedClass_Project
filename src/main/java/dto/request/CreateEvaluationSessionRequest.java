package dto.request;

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

    @NotBlank(message = "title is required")
    @Size(min = 3, max = 200, message = "title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "courseName is required")
    @Size(min = 2, max = 100, message = "courseName must be between 2 and 100 characters")
    private String courseName;

    @NotNull(message = "lecturerId is required")
    @Positive(message = "lecturerId must be a positive number")
    private Long lecturerId;

    @NotNull(message = "gradingStartAt is required")
    private LocalDateTime gradingStartAt;

    @NotNull(message = "gradingDeadlineAt is required")
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
        @Size(min = 2, max = 150, message = "criterion name must be between 2 and 150 characters")
        private String name;

        @Size(max = 300, message = "criterion description must not exceed 300 characters")
        private String description;

        @NotNull(message = "criterion maxScore is required")
        @DecimalMin(value = "0.01", message = "criterion maxScore must be greater than 0")
        private BigDecimal maxScore;

        @jakarta.validation.constraints.Min(value = 0, message = "criterion sortOrder must be at least 0")
        @jakarta.validation.constraints.Max(value = 999, message = "criterion sortOrder must not exceed 999")
        private Integer sortOrder;
    }
}
