package com.example.flippedclass.service;

import com.example.flippedclass.dto.NodeRequest;
import com.example.flippedclass.dto.NodeResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.NodeStatus;
import com.example.flippedclass.enums.NodeType;
import com.example.flippedclass.repository.NodeRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final LearningPathService learningPathService;

    public NodeService(NodeRepository nodeRepository, LearningPathService learningPathService) {
        this.nodeRepository = nodeRepository;
        this.learningPathService = learningPathService;
    }

    public List<NodeResponse> findByLearningPath(Long pathId) {
        learningPathService.getLearningPath(pathId);
        return nodeRepository.findByLearningPathIdOrderByDisplayOrderAsc(pathId).stream()
                .map(NodeResponse::from)
                .toList();
    }

    public NodeResponse findById(Long id) {
        return NodeResponse.from(getNode(id));
    }

    public NodeResponse create(Long pathId, NodeRequest request) {
        LearningPath learningPath = learningPathService.getLearningPath(pathId);
        validateRequest(request);
        if (nodeRepository.existsByLearningPathIdAndDisplayOrder(pathId, request.getDisplayOrder())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display order already exists in this learning path");
        }

        LearningNode node = new LearningNode();
        node.setLearningPath(learningPath);
        applyRequest(node, request);
        return NodeResponse.from(nodeRepository.save(node));
    }

    public NodeResponse update(Long id, NodeRequest request) {
        LearningNode node = getNode(id);
        validateRequest(request);
        Long pathId = node.getLearningPath().getId();
        if (nodeRepository.existsByLearningPathIdAndDisplayOrderAndIdNot(pathId, request.getDisplayOrder(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display order already exists in this learning path");
        }

        applyRequest(node, request);
        return NodeResponse.from(nodeRepository.save(node));
    }

    public void delete(Long id) {
        nodeRepository.delete(getNode(id));
    }

    public LearningNode getNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));
    }

    private void validateRequest(NodeRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node title is required");
        }
        if (request.getDisplayOrder() == null || request.getDisplayOrder() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display order must be greater than 0");
        }
        if (request.getEstimatedMinutes() != null && request.getEstimatedMinutes() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimated minutes cannot be negative");
        }
    }

    private void applyRequest(LearningNode node, NodeRequest request) {
        node.setTitle(request.getTitle().trim());
        node.setDescription(request.getDescription());
        node.setContent(request.getContent());
        node.setNodeType(request.getNodeType() == null ? NodeType.LESSON : request.getNodeType());
        node.setStatus(request.getStatus() == null ? NodeStatus.DRAFT : request.getStatus());
        node.setDisplayOrder(request.getDisplayOrder());
        node.setEstimatedMinutes(request.getEstimatedMinutes());
    }
}
