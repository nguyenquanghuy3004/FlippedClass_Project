package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupActivityCreateRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @Min(1)
    private Integer maxMembers;
    
    @Future
    private LocalDateTime deadline;
}
