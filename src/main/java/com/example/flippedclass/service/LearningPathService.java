package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LearningPathService {
    LearningPathResponse createLearningPath(Long spaceId, CreateLearningPathRequest request);

    List<LearningPathResponse> getLearningPath(Long spaceId);
    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId);

    LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId);
    public LearningPathResponse findById(Long id);

    LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request);
    public LearningPathResponse create(Long learningSpaceId, LearningPathRequest request);

    void archiveLearningPath(Long spaceId, Long pathId);
    public LearningPathResponse update(Long id, LearningPathRequest request);

    void restoreLearningPath(Long spaceId, Long pathId);
    public void delete(Long id);

    void deleteLearningPath(Long spaceId, Long pathId);

    void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request);
    public LearningPath getLearningPath(Long id);
}
