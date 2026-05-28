package com.example.flippedclass.dto;

import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import java.time.LocalDateTime;

public class LearningPathResponse {
    private Long id;
    private Long learningSpaceId;
    private String title;
    private String description;
    private LearningPathStatus status;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LearningPathResponse from(LearningPath learningPath) {
        LearningPathResponse response = new LearningPathResponse();
        response.id = learningPath.getId();
        response.learningSpaceId = learningPath.getLearningSpaceId();
        response.title = learningPath.getTitle();
        response.description = learningPath.getDescription();
        response.status = learningPath.getStatus();
        response.position = learningPath.getPosition();
        response.createdAt = learningPath.getCreatedAt();
        response.updatedAt = learningPath.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getLearningSpaceId() {
        return learningSpaceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LearningPathStatus getStatus() {
        return status;
    }

    public Integer getPosition() {
        return position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
