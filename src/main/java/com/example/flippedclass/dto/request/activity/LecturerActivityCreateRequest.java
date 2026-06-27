package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LecturerActivityCreateRequest {
    @NotNull(message = "Learning Path ID is required")
    private Long learningPathId;

    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @Min(value = 1, message = "Max members must be at least 1")
    private Integer maxMembers;
    
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}
