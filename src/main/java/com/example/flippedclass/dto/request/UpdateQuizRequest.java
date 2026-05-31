package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateQuizRequest {

    @Size(min = 3, max = 200, message = "title must be between 3 and 200 characters")
    private String title;

    @Size(max = 500, message = "description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "durationMinutes must be at least 1")
    @Max(value = 480, message = "durationMinutes must not exceed 480")
    private Integer durationMinutes;

    private Boolean active;

    @AssertTrue(message = "at least one field must be provided for update")
    public boolean isHasAtLeastOneField() {
        return title != null || description != null || durationMinutes != null || active != null;
    }
}
