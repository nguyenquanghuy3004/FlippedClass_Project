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
public class ActivityDetailResponse {
    private Long id;
    private Long classroomId;
    private String title;
    private String description;
    private Integer minMembersPerGroup;
    private Integer maxMembersPerGroup;
    private boolean allowLateSubmission;
    private boolean autoGroupEnabled;
    private LocalDateTime deadline;
    private ActivityStatus status;
    private LocalDateTime createdAt;
}
