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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Xử lý nghiệp vụ nối các node trong Learning Path
@Service
@RequiredArgsConstructor
public class NodeConnectionServiceImpl implements NodeConnectionService {

    private final NodeConnectionRepository connectionRepository;
    private final LearningPathService learningPathService;
    private final NodeService nodeService;

    // Lấy danh sách liên kết node theo Learning Path
    @Override
    public List<NodeConnectionResponse> findByLearningPath(Long pathId) {
        learningPathService.getLearningPath(pathId);
        return connectionRepository.findByLearningPathId(pathId).stream()
                .map(NodeConnectionResponse::from)
                .toList();
    }

    // Tạo liên kết mới và tránh nối sai Learning Path
    @Override
    public NodeConnectionResponse create(Long pathId, NodeConnectionRequest request) {
        LearningPath learningPath = learningPathService.getLearningPath(pathId);
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

    // Xóa liên kết node theo id
    @Override
    public void delete(Long pathId, Long id) {
        connectionRepository.delete(getConnectionInPath(pathId, id));
    }

    // Kiểm tra hai node đầu vào trước khi tạo liên kết
    private void validateRequiredNodeIds(NodeConnectionRequest request) {
        if (request.getSourceNodeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source node id is required");
        }
        if (request.getTargetNodeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target node id is required");
        }
    }

    // Đảm bảo node thuộc đúng Learning Path đang xử lý
    private void validateNodeBelongsToPath(LearningNode node, Long pathId, String message) {
        if (!Objects.equals(node.getLearningPath().getId(), pathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private NodeConnection getConnectionInPath(Long pathId, Long id) {
        learningPathService.getLearningPath(pathId);
        NodeConnection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node connection not found"));
        if (!Objects.equals(connection.getLearningPath().getId(), pathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node connection does not belong to this learning path");
        }
        return connection;
    }
}
