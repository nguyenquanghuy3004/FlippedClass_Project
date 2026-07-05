package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatRequest {
    @Min(value = 1, message = "activeSeconds must be at least 1")
    @Max(value = 120, message = "activeSeconds cannot exceed 120")
    private int activeSeconds;
}
