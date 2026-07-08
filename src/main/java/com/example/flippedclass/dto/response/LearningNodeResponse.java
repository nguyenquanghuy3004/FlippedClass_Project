package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LearningNodeResponse {
    private Long id;
    private String title;
    private String description;
    private Long learningPathId;
    private Long learningSpaceId;
    private String status;
    private String nodeType;
    private String content;
    private String starterCode;
    private String solutionCode;
    private Long prerequisiteNodeId;
    private java.util.List<java.util.Map<String, Object>> quizzes;
    private Boolean isOptional;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
