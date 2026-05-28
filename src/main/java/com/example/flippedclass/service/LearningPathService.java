package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.LearningPathResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final LearningSpaceRepository learningSpaceRepository;

    public LearningPathService(
            LearningPathRepository learningPathRepository,
            LearningSpaceRepository learningSpaceRepository
    ) {
        this.learningPathRepository = learningPathRepository;
        this.learningSpaceRepository = learningSpaceRepository;
    }

    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId) {
        return learningPathRepository.findByLearningSpace_IdOrderByPositionAsc(learningSpaceId).stream()
                .map(LearningPathResponse::from)
                .toList();
    }

    public LearningPathResponse findById(Long id) {
        return LearningPathResponse.from(getLearningPath(id));
    }

    public LearningPathResponse create(Long learningSpaceId, LearningPathRequest request) {
        LearningSpace learningSpace = getLearningSpace(learningSpaceId);
        LearningPath learningPath = new LearningPath();
        learningPath.setLearningSpace(learningSpace);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    public LearningPathResponse update(Long id, LearningPathRequest request) {
        LearningPath learningPath = getLearningPath(id);
        applyRequest(learningPath, request);
        return LearningPathResponse.from(learningPathRepository.save(learningPath));
    }

    public void delete(Long id) {
        learningPathRepository.delete(getLearningPath(id));
    }

    public LearningPath getLearningPath(Long id) {
        return learningPathRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning path not found"));
    }

    private LearningSpace getLearningSpace(Long id) {
        return learningSpaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning space not found"));
    }

    private void applyRequest(LearningPath learningPath, LearningPathRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Learning path title is required");
        }

        learningPath.setTitle(request.getTitle().trim());
        learningPath.setDescription(request.getDescription());
        learningPath.setStatus(request.getStatus() == null ? LearningPathStatus.DRAFT : request.getStatus());
        learningPath.setPosition(request.getPosition());
    }
}
