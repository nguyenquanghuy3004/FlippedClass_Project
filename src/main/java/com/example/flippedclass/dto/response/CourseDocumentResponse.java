package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.CourseDocument;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.enums.DocumentType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CourseDocumentResponse {
    private Long id;
    private Long learningPathId;
    private String learningPathTitle;
    private Long learningSpaceId;
    private String learningSpaceName;
    private String title;
    private DocumentType documentType;
    private String url;
    private String description;
    private LocalDateTime createdAt;
    private Long nodeId;

    public static CourseDocumentResponse from(CourseDocument document) {
        LearningPath learningPath = document.getLearningPath();
        LearningSpace learningSpace = learningPath != null ? learningPath.getLearningSpace() : null;

        CourseDocumentResponse response = new CourseDocumentResponse();
        response.id = document.getId();
        response.learningPathId = learningPath != null ? learningPath.getId() : null;
        response.learningPathTitle = learningPath != null ? learningPath.getTitle() : null;
        response.learningSpaceId = learningSpace != null ? learningSpace.getId() : null;
        response.learningSpaceName = learningSpace != null ? learningSpace.getName() : null;
        response.title = document.getTitle();
        response.documentType = parseDocumentType(document.getDocumentType());
        response.url = document.getUrl();
        response.description = document.getDescription();
        response.createdAt = document.getCreatedAt();
        return response;
    }

    public static CourseDocumentResponse from(com.example.flippedclass.entity.LearningNodeItem item) {
        com.example.flippedclass.entity.LearningNode node = item.getLearningNode();
        LearningPath learningPath = node != null ? node.getLearningPath() : null;
        LearningSpace learningSpace = learningPath != null ? learningPath.getLearningSpace() : null;

        CourseDocumentResponse response = new CourseDocumentResponse();
        response.id = item.getId();
        response.nodeId = node != null ? node.getId() : null;
        response.learningPathId = learningPath != null ? learningPath.getId() : null;
        response.learningPathTitle = learningPath != null ? learningPath.getTitle() : null;
        response.learningSpaceId = learningSpace != null ? learningSpace.getId() : null;
        response.learningSpaceName = learningSpace != null ? learningSpace.getName() : null;
        response.title = item.getTitle();
        response.documentType = parseDocumentType(item.getItemType() != null ? item.getItemType().name() : null);
        response.url = item.getUrl();
        response.description = item.getContent();
        response.createdAt = node != null ? node.getCreatedAt() : null;
        return response;
    }

    private static DocumentType parseDocumentType(String documentType) {
        if (documentType == null || documentType.isBlank()) {
            return null;
        }
        try {
            return DocumentType.valueOf(documentType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return DocumentType.OTHER;
        }
    }
}
