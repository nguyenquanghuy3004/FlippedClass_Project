package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.CourseDocument;
import com.example.flippedclass.enums.DocumentType;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
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
}
