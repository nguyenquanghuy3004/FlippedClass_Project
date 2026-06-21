package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.NodeRequest;
import com.example.flippedclass.dto.response.NodeResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.NodeStatus;
import com.example.flippedclass.enums.NodeType;
import com.example.flippedclass.repository.NodeRepository;
import com.example.flippedclass.service.LearningPathService;
import com.example.flippedclass.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final LearningPathService learningPathService;

    @Override
    public List<NodeResponse> findByLearningPath(Long pathId) {
        learningPathService.getLearningPathEntity(pathId);
        return nodeRepository.findByLearningPathIdOrderByDisplayOrderAsc(pathId).stream()
                .map(NodeResponse::from)
                .toList();
    }

    @Override
    public NodeResponse findById(Long pathId, Long nodeId) {
        return NodeResponse.from(getNodeInLearningPath(pathId, nodeId));
    }

    @Override
    public NodeResponse create(Long pathId, NodeRequest request) {
        LearningPath learningPath = learningPathService.getLearningPathEntity(pathId);
        validateRequest(request);
        if (nodeRepository.existsByLearningPathIdAndDisplayOrder(pathId, request.getDisplayOrder())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display order already exists in this learning path");
        }

        LearningNode node = new LearningNode();
        node.setLearningPath(learningPath);
        applyRequest(node, request);
        return NodeResponse.from(nodeRepository.save(node));
    }

    @Override
    public NodeResponse update(Long pathId, Long nodeId, NodeRequest request) {
        LearningNode node = getNodeInLearningPath(pathId, nodeId);
        validateRequest(request);
        if (nodeRepository.existsByLearningPathIdAndDisplayOrderAndIdNot(pathId, request.getDisplayOrder(), nodeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display order already exists in this learning path");
        }

        applyRequest(node, request);
        return NodeResponse.from(nodeRepository.save(node));
    }

    @Override
    public void delete(Long pathId, Long nodeId) {
        nodeRepository.delete(getNodeInLearningPath(pathId, nodeId));
    }

    @Override
    public LearningNode getNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));
    }

    private LearningNode getNodeInLearningPath(Long pathId, Long nodeId) {
        LearningNode node = getNode(nodeId);
        if (!pathId.equals(node.getLearningPath().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node does not belong to this learning path");
        }
        return node;
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
        node.setNodeType((request.getNodeType() == null ? NodeType.VIDEO : request.getNodeType()).name());
        node.setStatus((request.getStatus() == null ? NodeStatus.DRAFT : request.getStatus()).name());
        node.setDisplayOrder(request.getDisplayOrder());
        node.setEstimatedMinutes(request.getEstimatedMinutes());
    }
}
