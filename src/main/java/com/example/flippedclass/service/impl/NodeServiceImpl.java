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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Xử lý nghiệp vụ tạo và sắp xếp node học tập
@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final LearningPathService learningPathService;

    // Lấy danh sách node theo thứ tự hiển thị
    @Override
    public List<NodeResponse> findByLearningPath(Long pathId) {
        learningPathService.getLearningPath(pathId);
        return nodeRepository.findByLearningPathIdOrderByDisplayOrderAsc(pathId).stream()
                .map(NodeResponse::from)
                .toList();
    }

    // Lấy chi tiết node học tập theo id
    @Override
    public NodeResponse findById(Long pathId, Long id) {
        return NodeResponse.from(getNodeInPath(pathId, id));
    }

    // Tạo node mới và kiểm tra trùng thứ tự hiển thị
    @Override
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

    // Cập nhật node và giữ thứ tự không bị trùng
    @Override
    public NodeResponse update(Long pathId, Long id, NodeRequest request) {
        LearningNode node = getNodeInPath(pathId, id);
        validateRequest(request);
        if (nodeRepository.existsByLearningPathIdAndDisplayOrderAndIdNot(pathId, request.getDisplayOrder(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display order already exists in this learning path");
        }

        applyRequest(node, request);
        return NodeResponse.from(nodeRepository.save(node));
    }

    // Xóa node học tập theo id
    @Override
    public void delete(Long pathId, Long id) {
        nodeRepository.delete(getNodeInPath(pathId, id));
    }

    // Kiểm tra và lấy node trước khi xử lý
    @Override
    public LearningNode getNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));
    }

    private LearningNode getNodeInPath(Long pathId, Long id) {
        learningPathService.getLearningPath(pathId);
        LearningNode node = getNode(id);
        if (!Objects.equals(node.getLearningPath().getId(), pathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node does not belong to this learning path");
        }
        return node;
    }

    // Kiểm tra dữ liệu node trước khi lưu
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

    // Gán dữ liệu từ request vào node học tập
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
