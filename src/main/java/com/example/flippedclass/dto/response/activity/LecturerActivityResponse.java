package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.ActivityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LecturerActivityResponse {
    private Long id;
    private Long learningNodeId;
    private String learningNodeTitle;
    private String title;
    private String description;
    private Integer maxMembers;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private ActivityStatus status;
    private int totalGroups;
    private int totalStudents;
    private int submittedGroups;
}
