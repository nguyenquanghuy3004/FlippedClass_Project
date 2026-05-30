package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.LearningPathStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<LearningNodeResponse> nodes; // ADDED THIS FIELD
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
