package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningNode;
import java.time.LocalDateTime;

public class NodeResponse {
    private Long id;
    private Long learningPathId;
    private String title;
    private String description;
    private String content;
    private String nodeType;
    private String status;
    private Integer displayOrder;
    private Integer estimatedMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NodeResponse from(LearningNode node) {
        NodeResponse response = new NodeResponse();
        response.id = node.getId();
        response.learningPathId = node.getLearningPath().getId();
        response.title = node.getTitle();
        response.description = node.getDescription();
        response.content = node.getContent();
        response.nodeType = node.getNodeType();
        response.status = node.getStatus();
        response.displayOrder = node.getDisplayOrder();
        response.estimatedMinutes = node.getEstimatedMinutes();
        response.createdAt = node.getCreatedAt();
        response.updatedAt = node.getUpdatedAt();
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

    public String getContent() {
        return content;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
