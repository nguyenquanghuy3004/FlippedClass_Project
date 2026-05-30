package com.example.flippedclass.service.impl;

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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Xử lý nghiệp vụ tạo và quản lý Learning Path
@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final LearningSpaceRepository learningSpaceRepository;
    private final UserRepository userRepository;

    // Lấy danh sách Learning Path theo Learning Space
    @Override
    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId) {
        return learningPathRepository.findByLearningSpace_IdOrderByPositionAsc(learningSpaceId).stream()
                .map(LearningPathResponse::from)
                .toList();
    }

    // Lấy chi tiết Learning Path theo id
    @Override
    public LearningPathResponse findById(Long learningSpaceId, Long id) {
        return LearningPathResponse.from(getLearningPathInSpace(learningSpaceId, id));
    }

    // Tạo mới Learning Path và kiểm tra dữ liệu lớp học
    @Override
    public LearningPathResponse create(Long learningSpaceId, LearningPathRequest request) {
        if (request.getLearningSpaceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning space id is required");
        }
        if (!request.getLearningSpaceId().equals(learningSpaceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning space id does not match path");
        }
        if (request.getLecturerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lecturer id is required");
        }

        LearningSpace learningSpace = getLearningSpace(request.getLearningSpaceId());
        User lecturer = getLecturer(request.getLecturerId());
        LearningPath learningPath = new LearningPath();
        learningPath.setLearningSpace(learningSpace);
        learningPath.setLecturer(lecturer);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    // Cập nhật thông tin Learning Path
    @Override
    public LearningPathResponse update(Long learningSpaceId, Long id, LearningPathRequest request) {
        LearningPath learningPath = getLearningPathInSpace(learningSpaceId, id);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    // Xóa Learning Path theo id
    @Override
    public void delete(Long learningSpaceId, Long id) {
        learningPathRepository.delete(getLearningPathInSpace(learningSpaceId, id));
    }

    // Kiểm tra và lấy Learning Path trước khi xử lý
    @Override
    public LearningPath getLearningPath(Long id) {
        return learningPathRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning path not found"));
    }

    private LearningSpace getLearningSpace(Long id) {
        return learningSpaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning space not found"));
    }

    private User getLecturer(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecturer not found"));
    }

    private LearningPath getLearningPathInSpace(Long learningSpaceId, Long id) {
        getLearningSpace(learningSpaceId);
        LearningPath learningPath = getLearningPath(id);
        if (!Objects.equals(learningPath.getLearningSpaceId(), learningSpaceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning path does not belong to this learning space");
        }
        return learningPath;
    }

    // Kiểm tra dữ liệu trước khi lưu Learning Path
    private void applyRequest(LearningPath learningPath, LearningPathRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning path title is required");
        }

        if (request.getLecturerId() != null && !request.getLecturerId().equals(learningPath.getLecturerId())) {
            learningPath.setLecturer(getLecturer(request.getLecturerId()));
        }
        learningPath.setTitle(request.getTitle().trim());
        learningPath.setDescription(request.getDescription());
        learningPath.setStatus(request.getStatus() == null ? LearningPathStatus.DRAFT : request.getStatus());
        learningPath.setPosition(request.getPosition());
    }
}
