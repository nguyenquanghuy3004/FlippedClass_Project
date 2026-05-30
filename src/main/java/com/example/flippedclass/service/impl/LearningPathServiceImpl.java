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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LearningPathServiceImpl implements LearningPathService {

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
    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId) {
        return learningPathRepository.findByLearningSpace_IdOrderByPositionAsc(learningSpaceId).stream()
                .map(LearningPathResponse::from)
                .toList();
    }

    @Override
    public LearningPathResponse findById(Long id) {
        return LearningPathResponse.from(getLearningPath(id));
    }

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

    @Override
    public LearningPathResponse update(Long id, LearningPathRequest request) {
        LearningPath learningPath = getLearningPath(id);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    @Override
    public void delete(Long id) {
        learningPathRepository.delete(getLearningPath(id));
    }

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
