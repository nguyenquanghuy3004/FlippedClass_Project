package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.NodeConnectionRepository;
import com.example.flippedclass.repository.NodeProgressRepository;
import com.example.flippedclass.service.LearningPathService;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.enums.LearningSpaceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    @Autowired
    private LearningSpaceRepository learningSpaceRepository;

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private NodeConnectionRepository nodeConnectionRepository;

    @Autowired
    private NodeProgressRepository nodeProgressRepository;

    @Override
    public LearningPath getLearningPathEntity(Long id) {
        return learningPathRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Path"));
    }

    @Override
    @Transactional
    public LearningPathResponse createLearningPath(Long spaceId, CreateLearningPathRequest request) {
        LearningSpace space = learningSpaceRepository.findByIdAndStatus(spaceId, LearningSpaceStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning or Space đã bị xóa"));

        Integer nextPosition = learningPathRepository
                .findFirstByLearningSpaceIdAndStatusOrderByPositionDesc(spaceId, LearningPathStatus.ACTIVE)
                .map(lp -> lp.getPosition() + 1)
                .orElse(1);

        LearningPath path = new LearningPath();
        path.setTitle(request.getTitle());
        path.setDescription(request.getDescription());
        path.setPosition(nextPosition);
        path.setStatus(LearningPathStatus.ACTIVE);
        path.setLearningSpace(space);
        path.setLecturer(space.getOwner());

        return toResponse(learningPathRepository.save(path), null);
    }

    @Override
    public List<LearningPathResponse> getLearningPath(Long spaceId, Long studentId) {
        return learningPathRepository
                .findByLearningSpaceIdAndStatusOrderByPositionAsc(spaceId, LearningPathStatus.ACTIVE)
                .stream()
                .map(path -> toResponse(path, studentId))
                .toList();
    }

    @Override
    public LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId, Long studentId) {
        return toResponse(findPathInSpace(spaceId, pathId), studentId);
    }

    @Override
    @Transactional
    public LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request) {
        LearningPath path = findPathInSpace(spaceId, pathId);

        if (path.getStatus() == LearningPathStatus.ARCHIVED) {
            throw new IllegalArgumentException("Không thể chỉnh sửa roadmap đã lưu trữ. Hãy khôi phục trước.");
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            path.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            path.setDescription(request.getDescription());
        }

        return toResponse(path, null);
    }

    @Override
    @Transactional
    public void archiveLearningPath(Long spaceId, Long pathId) {
        LearningPath path = findPathInSpace(spaceId, pathId);
        if (path.getStatus() != LearningPathStatus.ACTIVE) {
            throw new IllegalArgumentException("Chỉ có thể lưu trữ roadmap đang hoạt động");
        }
        path.setStatus(LearningPathStatus.ARCHIVED);
    }

    @Override
    @Transactional
    public void restoreLearningPath(Long spaceId, Long pathId) {
        LearningPath path = findPathInSpace(spaceId, pathId);
        if (path.getStatus() != LearningPathStatus.ARCHIVED && path.getStatus() != LearningPathStatus.DELETED) {
            throw new IllegalArgumentException("Chỉ có thể khôi phục roadmap đã lưu trữ hoặc đã xóa");
        }
        path.setStatus(LearningPathStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deleteLearningPathModul(Long spaceId, Long pathId) {
        findPathInSpace(spaceId, pathId).setStatus(LearningPathStatus.DELETED);
    }

    @Override
    @Transactional
    public void deleteAllLearningPaths(Long spaceId) {
        List<LearningPath> paths = learningPathRepository.findByLearningSpaceIdAndStatusOrderByPositionAsc(spaceId, LearningPathStatus.ACTIVE);
        for (LearningPath path : paths) {
            path.setStatus(LearningPathStatus.DELETED);
        }
    }

    @Override
    @Transactional
    public void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request) {
        List<Long> orderedIds = request.getOrderedIds();
        if (orderedIds == null || orderedIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách id sắp xếp không được để trống");
        }

        for (int i = 0; i < orderedIds.size(); i++) {
            findPathInSpace(spaceId, orderedIds.get(i)).setPosition(i + 1);
        }
    }

    private LearningPath findPathInSpace(Long spaceId, Long pathId) {
        return learningPathRepository.findByIdAndLearningSpaceId(pathId, spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Path trong Learning Space này"));
    }

    private LearningPathResponse toResponse(LearningPath path, Long studentId) {
        List<LearningNodeResponse> nodeResponses = new java.util.ArrayList<>();
        if (path.getNodes() != null) {
            nodeResponses = path.getNodes().stream().map(node -> {
                Long prereqId = null;
                var connections = nodeConnectionRepository.findByTargetNodeId(node.getId());
                if (!connections.isEmpty()) {
                    prereqId = connections.get(0).getSourceNode().getId();
                }

                String computedStatus = node.getStatus();
                if (studentId != null && prereqId != null && !"DOCUMENT".equals(node.getNodeType())) {
                    var progress = nodeProgressRepository.findByStudentIdAndLearningNodeId(studentId, prereqId).orElse(null);
                    if (progress == null || !progress.getStatus().name().equals("COMPLETED")) {
                        computedStatus = "LOCKED";
                    }
                }

                return LearningNodeResponse.builder()
                        .id(node.getId())
                        .title(node.getTitle())
                        .description(node.getDescription())
                        .learningPathId(path.getId())
                        .learningSpaceId(path.getLearningSpace() != null ? path.getLearningSpace().getId() : null)
                        .status(computedStatus)
                        .nodeType(node.getNodeType())
                        .prerequisiteNodeId(prereqId)
                        .createdAt(node.getCreatedAt())
                        .updatedAt(node.getUpdatedAt())
                        .build();
            }).toList();
        }

        return LearningPathResponse.builder()
                .id(path.getId())
                .title(path.getTitle())
                .description(path.getDescription())
                .position(path.getPosition())
                .status(path.getStatus())
                .learningSpaceId(path.getLearningSpace().getId())
                .nodes(nodeResponses)
                .createdAt(path.getCreatedAt())
                .updatedAt(path.getUpdatedAt())
                .build();
    }

    @Override
    public List<LearningPathResponse> getDeletedLearningPaths(Long spaceId) {
        return learningPathRepository
                .findByLearningSpaceIdAndStatusInOrderByPositionAsc(spaceId, List.of(LearningPathStatus.ARCHIVED, LearningPathStatus.DELETED))
                .stream()
                .map(path -> toResponse(path, null))
                .toList();
    }
}
