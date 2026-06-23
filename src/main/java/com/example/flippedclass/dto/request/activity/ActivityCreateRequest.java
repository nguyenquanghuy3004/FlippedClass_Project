package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 1, message = "Min members must be at least 1")
    private Integer minMembersPerGroup = 1;

    @NotNull(message = "Max members is required")
    @Min(value = 1, message = "Max members must be at least 1")
    private Integer maxMembersPerGroup;

    private boolean allowLateSubmission = false;

    private boolean autoGroupEnabled = false;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}
