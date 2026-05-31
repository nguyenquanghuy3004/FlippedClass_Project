package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.NodeConnectionRequest;
import com.example.flippedclass.dto.response.NodeConnectionResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.NodeConnection;
import com.example.flippedclass.repository.NodeConnectionRepository;
import com.example.flippedclass.service.LearningPathService;
import com.example.flippedclass.service.NodeConnectionService;
import com.example.flippedclass.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NodeConnectionServiceImpl implements NodeConnectionService {

    private final NodeConnectionRepository connectionRepository;
    private final LearningPathService learningPathService;
    private final NodeService nodeService;

    @Override
    public List<NodeConnectionResponse> findByLearningPath(Long pathId) {
        learningPathService.getLearningPathEntity(pathId);
        return connectionRepository.findByLearningPathId(pathId).stream()
                .map(NodeConnectionResponse::from)
                .toList();
    }

    @Override
    public NodeConnectionResponse create(Long pathId, NodeConnectionRequest request) {
        LearningPath learningPath = learningPathService.getLearningPathEntity(pathId);
        validateRequiredNodeIds(request);
        if (Objects.equals(request.getSourceNodeId(), request.getTargetNodeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source node and target node must be different");
        }

        LearningNode sourceNode = nodeService.getNode(request.getSourceNodeId());
        LearningNode targetNode = nodeService.getNode(request.getTargetNodeId());
        validateNodeBelongsToPath(sourceNode, pathId, "Source node does not belong to this learning path");
        validateNodeBelongsToPath(targetNode, pathId, "Target node does not belong to this learning path");

        if (connectionRepository.existsByLearningPathIdAndSourceNodeIdAndTargetNodeId(
                pathId,
                request.getSourceNodeId(),
                request.getTargetNodeId()
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Node connection already exists");
        }

        NodeConnection connection = new NodeConnection();
        connection.setLearningPath(learningPath);
        connection.setSourceNode(sourceNode);
        connection.setTargetNode(targetNode);
        connection.setConditionType(request.getConditionType());
        connection.setConditionValue(request.getConditionValue());
        return NodeConnectionResponse.from(connectionRepository.save(connection));
    }

    @Override
    public NodeConnectionResponse update(Long pathId, Long connectionId, NodeConnectionRequest request) {
        NodeConnection connection = getConnectionInLearningPath(pathId, connectionId);
        validateRequiredNodeIds(request);
        if (Objects.equals(request.getSourceNodeId(), request.getTargetNodeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source node and target node must be different");
        }

        LearningNode sourceNode = nodeService.getNode(request.getSourceNodeId());
        LearningNode targetNode = nodeService.getNode(request.getTargetNodeId());
        validateNodeBelongsToPath(sourceNode, pathId, "Source node does not belong to this learning path");
        validateNodeBelongsToPath(targetNode, pathId, "Target node does not belong to this learning path");

        boolean sameNodePair = Objects.equals(connection.getSourceNode().getId(), request.getSourceNodeId())
                && Objects.equals(connection.getTargetNode().getId(), request.getTargetNodeId());
        if (!sameNodePair && connectionRepository.existsByLearningPathIdAndSourceNodeIdAndTargetNodeId(
                pathId,
                request.getSourceNodeId(),
                request.getTargetNodeId()
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Node connection already exists");
        }

        connection.setSourceNode(sourceNode);
        connection.setTargetNode(targetNode);
        connection.setConditionType(request.getConditionType());
        connection.setConditionValue(request.getConditionValue());
        return NodeConnectionResponse.from(connectionRepository.save(connection));
    }

    @Override
    public void delete(Long pathId, Long connectionId) {
        connectionRepository.delete(getConnectionInLearningPath(pathId, connectionId));
    }

    private NodeConnection getConnectionInLearningPath(Long pathId, Long connectionId) {
        NodeConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node connection not found"));
        if (!Objects.equals(connection.getLearningPath().getId(), pathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node connection does not belong to this learning path");
        }
        return connection;
    }

    private void validateRequiredNodeIds(NodeConnectionRequest request) {
        if (request.getSourceNodeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source node id is required");
        }
        if (request.getTargetNodeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target node id is required");
        }
    }

    private void validateNodeBelongsToPath(LearningNode node, Long pathId, String message) {
        if (!Objects.equals(node.getLearningPath().getId(), pathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
