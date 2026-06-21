package com.example.flippedclass.dto.request.activity;

import com.example.flippedclass.enums.ActivityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ActivityStatus status;
}
