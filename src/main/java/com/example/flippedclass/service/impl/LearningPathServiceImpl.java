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
import com.example.flippedclass.service.LearningPathService;
import enums.LearningPathStatus;
import enums.LearningSpaceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {
    @Autowired
     LearningSpaceRepository learningSpaceRepository;
    @Autowired
     LearningPathRepository learningPathRepository;


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
        path.setLecturer(space.getOwner()); // Fix: Set lecturer to avoid DB constraint violation

        return toResponse(learningPathRepository.save(path));
    }

    @Override
    public List<LearningPathResponse> getLearningPath(Long spaceId) {
        return learningPathRepository
                .findByLearningSpaceIdAndStatusOrderByPositionAsc(spaceId, LearningPathStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId) {
        return toResponse(findPathInSpace(spaceId, pathId));
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

        return toResponse(path); // Hibernate tự động update dữ liệu nhờ @Transactional
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
    public void deleteLearningPath(Long spaceId, Long pathId) {
        findPathInSpace(spaceId, pathId).setStatus(LearningPathStatus.DELETED);
    }

    //REORDER
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

    // helper
    private LearningPath findPathInSpace(Long spaceId, Long pathId) {
        return learningPathRepository.findByIdAndLearningSpaceId(pathId, spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Path trong Learning Space này"));
    }

    private LearningPathResponse toResponse(LearningPath path) {
        List<LearningNodeResponse> nodeResponses = new java.util.ArrayList<>();
        if (path.getNodes() != null) {
            nodeResponses = path.getNodes().stream().map(node -> 
                com.example.flippedclass.dto.response.LearningNodeResponse.builder()
                    .id(node.getId())
                    .title(node.getTitle())
                    .description(node.getDescription())
                    .learningPathId(path.getId())
                    .status(node.getStatus())
                    .nodeType(node.getNodeType())
                    .createdAt(node.getCreatedAt())
                    .updatedAt(node.getUpdatedAt())
                    .build()
            ).toList();
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
                .findByLearningSpaceIdAndStatusOrderByPositionAsc(spaceId, LearningPathStatus.DELETED)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
