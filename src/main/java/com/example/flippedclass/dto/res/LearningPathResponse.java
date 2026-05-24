package com.example.flippedclass.dto.res;

import enums.LearningPathStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LearningPathResponse {
    private Long id;
    private String title;
    private String description;
    private Integer position;
    private LearningPathStatus status;
    private Long learningSpaceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
