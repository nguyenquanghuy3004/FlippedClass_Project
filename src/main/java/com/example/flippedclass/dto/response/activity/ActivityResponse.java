package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private Long id;
    private Long classroomId;
    private String title;
    private ActivityStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}
