package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LearningPathService;

import com.example.flippedclass.enums.LearningSpaceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {
    @Autowired
     LearningSpaceRepository learningSpaceRepository;
    @Autowired
     LearningPathRepository learningPathRepository;


    private final LearningPathRepository learningPathRepository;
    private final LearningSpaceRepository learningSpaceRepository;
    private final UserRepository userRepository;

    public LearningPathServiceImpl(
            LearningPathRepository learningPathRepository,
            LearningSpaceRepository learningSpaceRepository,
            UserRepository userRepository
    ) {
        this.learningPathRepository = learningPathRepository;
        this.learningSpaceRepository = learningSpaceRepository;
        this.userRepository = userRepository;
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
        path.setLecturer(space.getOwner()); // Fix: Set lecturer to avoid DB constraint violation

        return toResponse(learningPathRepository.save(path));
    }

    @Override
    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId) {
        return learningPathRepository.findByLearningSpace_IdOrderByPositionAsc(learningSpaceId).stream()
                .map(LearningPathResponse::from)
    public List<LearningPathResponse> getLearningPath(Long spaceId) {
        return learningPathRepository
                .findByLearningSpaceIdAndStatusOrderByPositionAsc(spaceId, LearningPathStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public LearningPathResponse findById(Long id) {
        return LearningPathResponse.from(getLearningPath(id));
    public LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId) {
        return toResponse(findPathInSpace(spaceId, pathId));
    }


    @Override
    public LearningPathResponse create(Long learningSpaceId, LearningPathRequest request) {
        if (request.getLearningSpaceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning space id is required");
    @Transactional
    public LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request) {
        LearningPath path = findPathInSpace(spaceId, pathId);

        if (path.getStatus() == LearningPathStatus.ARCHIVED) {
            throw new IllegalArgumentException("Không thể chỉnh sửa roadmap đã lưu trữ. Hãy khôi phục trước.");
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            path.setTitle(request.getTitle());
        if (!request.getLearningSpaceId().equals(learningSpaceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning space id does not match path");
        }
        if (request.getDescription() != null) {
            path.setDescription(request.getDescription());
        if (request.getLecturerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lecturer id is required");
        }

        return toResponse(path); // Hibernate tự động update dữ liệu nhờ @Transactional
        LearningSpace learningSpace = getLearningSpace(request.getLearningSpaceId());
        User lecturer = getLecturer(request.getLecturerId());
        LearningPath learningPath = new LearningPath();
        learningPath.setLearningSpace(learningSpace);
        learningPath.setLecturer(lecturer);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
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

    public LearningPathResponse update(Long id, LearningPathRequest request) {
        LearningPath learningPath = getLearningPath(id);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    @Override
    @Transactional
    public void restoreLearningPath(Long spaceId, Long pathId) {
        LearningPath path = findPathInSpace(spaceId, pathId);
        if (path.getStatus() != LearningPathStatus.ARCHIVED) {
            throw new IllegalArgumentException("Chỉ có thể khôi phục roadmap đã lưu trữ");
        }
        path.setStatus(LearningPathStatus.ACTIVE);
    }

    public void delete(Long id) {
        learningPathRepository.delete(getLearningPath(id));
    }

    @Override
    @Transactional
    public void deleteLearningPath(Long spaceId, Long pathId) {
        findPathInSpace(spaceId, pathId).setStatus(LearningPathStatus.DELETED);
    public LearningPath getLearningPath(Long id) {
        return learningPathRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning path not found"));
    }

    //REORDER
    @Override
    @Transactional
    public void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request) {
        List<Long> orderedIds = request.getOrderedIds();
        if (orderedIds == null || orderedIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách id sắp xếp không được để trống");
        }
    private LearningSpace getLearningSpace(Long id) {
        return learningSpaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning space not found"));
    }

    private User getLecturer(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecturer not found"));
        for (int i = 0; i < orderedIds.size(); i++) {
            findPathInSpace(spaceId, orderedIds.get(i)).setPosition(i + 1);
        }
    }

    private void applyRequest(LearningPath learningPath, LearningPathRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning path title is required");
        }
    // helper
    private LearningPath findPathInSpace(Long spaceId, Long pathId) {
        return learningPathRepository.findByIdAndLearningSpaceId(pathId, spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Path trong Learning Space này"));
    }

        if (request.getLecturerId() != null && !request.getLecturerId().equals(learningPath.getLecturerId())) {
            learningPath.setLecturer(getLecturer(request.getLecturerId()));
    private LearningPathResponse toResponse(LearningPath path) {
        List<LearningNodeResponse> nodeResponses = new java.util.ArrayList<>();
        if (path.getNodes() != null) {
            nodeResponses = path.getNodes().stream().map(node ->
                com.example.flippedclass.dto.response.LearningNodeResponse.builder()
                    .id(node.getId())
                    .title(node.getTitle())
//                    .description(node.getDescription())
                    .learningPathId(path.getId())
                    .status(node.getStatus())
                    .nodeType(node.getNodeType())
                    .createdAt(node.getCreatedAt())
                    .updatedAt(node.getUpdatedAt())
                    .build()
            ).toList();
        }
        learningPath.setTitle(request.getTitle().trim());
        learningPath.setDescription(request.getDescription());
        learningPath.setStatus(request.getStatus() == null ? LearningPathStatus.DRAFT : request.getStatus());
        learningPath.setPosition(request.getPosition());

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
}
