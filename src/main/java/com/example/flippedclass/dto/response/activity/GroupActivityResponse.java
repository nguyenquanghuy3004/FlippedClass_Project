package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.ActivityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupActivityResponse {
    private Long id;
    private Long learningNodeId;
    private String title;
    private String description;
    private Integer maxMembers;
    private LocalDateTime deadline;
    private ActivityStatus status;
}
