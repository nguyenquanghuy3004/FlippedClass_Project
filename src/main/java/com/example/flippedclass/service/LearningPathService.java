package com.example.flippedclass.service;

import com.example.flippedclass.dto.req.CreateLearningPathRequest;
import com.example.flippedclass.dto.req.ReorderLearningPathRequest;
import com.example.flippedclass.dto.req.UpdateLearningPathRequest;
import com.example.flippedclass.dto.res.LearningPathResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LearningPathService {
    LearningPathResponse createLearningPath(Long spaceId, CreateLearningPathRequest request);

    List<LearningPathResponse> getLearningPath(Long spaceId);

    LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId);

    LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request);

    void archiveLearningPath(Long spaceId, Long pathId);

    void restoreLearningPath(Long spaceId, Long pathId);

    void deleteLearningPath(Long spaceId, Long pathId);

    void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request);
}
