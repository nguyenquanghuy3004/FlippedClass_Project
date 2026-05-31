package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.CourseDocument;

import java.time.LocalDateTime;

public class CourseDocumentResponse {
    private Long id;
    private Long learningPathId;
    private String title;
    private String description;
    private String type;
    private String url;
    private LocalDateTime createdAt;

    public static CourseDocumentResponse from(CourseDocument doc) {
        CourseDocumentResponse response = new CourseDocumentResponse();
        response.id = doc.getId();
        response.learningPathId = doc.getLearningPath().getId();
        response.title = doc.getTitle();
        response.description = doc.getDescription();
        response.type = doc.getDocumentType();
        response.url = doc.getUrl();
        response.createdAt = doc.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getLearningPathId() {
        return learningPathId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
