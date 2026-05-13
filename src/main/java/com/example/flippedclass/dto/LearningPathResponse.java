package com.example.flippedclass.dto;

import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import java.time.LocalDateTime;

public class LearningPathResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private LearningPathStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LearningPathResponse from(LearningPath learningPath) {
        LearningPathResponse response = new LearningPathResponse();
        response.id = learningPath.getId();
        response.courseId = learningPath.getCourse().getId();
        response.title = learningPath.getTitle();
        response.description = learningPath.getDescription();
        response.status = learningPath.getStatus();
        response.createdAt = learningPath.getCreatedAt();
        response.updatedAt = learningPath.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
