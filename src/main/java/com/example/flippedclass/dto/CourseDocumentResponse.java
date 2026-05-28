package com.example.flippedclass.dto;

import com.example.flippedclass.entity.CourseDocument;
import com.example.flippedclass.enums.DocumentType;

import java.time.LocalDateTime;

public class CourseDocumentResponse {
    private Long id;
    private Long learningPathId;
    private String title;
    private DocumentType documentType;
    private String url;
    private String description;
    private LocalDateTime createdAt;

    public static CourseDocumentResponse from(CourseDocument document) {
        CourseDocumentResponse response = new CourseDocumentResponse();
        response.id = document.getId();
        response.learningPathId = document.getLearningPath().getId();
        response.title = document.getTitle();
        response.documentType = document.getDocumentType();
        response.url = document.getUrl();
        response.description = document.getDescription();
        response.createdAt = document.getCreatedAt();
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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
