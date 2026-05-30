package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.NodeConnection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
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
}
