package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityUpdateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}
