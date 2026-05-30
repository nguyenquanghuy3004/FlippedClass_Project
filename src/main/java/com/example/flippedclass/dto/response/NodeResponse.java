package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.enums.NodeStatus;
import com.example.flippedclass.enums.NodeType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class NodeResponse {
    private Long id;
    private Long learningPathId;
    private String title;
    private String description;
    private String content;
    private NodeType nodeType;
    private NodeStatus status;
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
}
