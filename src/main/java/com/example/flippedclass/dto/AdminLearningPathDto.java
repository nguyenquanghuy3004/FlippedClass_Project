package com.example.flippedclass.dto;

import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.enums.VisibilityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminLearningPathDto {
    private Long id;
    private String title;
    private String description;
    private Long spaceId;
    private String spaceName;
    private Long lecturerId;
    private String lecturerEmail;
    private LearningPathStatus status;
    private VisibilityType visibility;
    private Integer nodeCount;
    private LocalDateTime createdAt;
}
