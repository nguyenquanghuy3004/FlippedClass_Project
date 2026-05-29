package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.NodeConnection;
import java.time.LocalDateTime;

public class NodeConnectionResponse {
    private Long id;
    private Long learningPathId;
    private Long sourceNodeId;
    private Long targetNodeId;
    private String conditionType;
    private String conditionValue;
    private LocalDateTime createdAt;

    public static NodeConnectionResponse from(NodeConnection connection) {
        NodeConnectionResponse response = new NodeConnectionResponse();
        response.id = connection.getId();
        response.learningPathId = connection.getLearningPath().getId();
        response.sourceNodeId = connection.getSourceNode().getId();
        response.targetNodeId = connection.getTargetNode().getId();
        response.conditionType = connection.getConditionType();
        response.conditionValue = connection.getConditionValue();
        response.createdAt = connection.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getLearningPathId() {
        return learningPathId;
    }

    public Long getSourceNodeId() {
        return sourceNodeId;
    }

    public Long getTargetNodeId() {
        return targetNodeId;
    }

    public String getConditionType() {
        return conditionType;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
