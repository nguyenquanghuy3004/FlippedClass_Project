package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;

import java.util.List;

public interface LearningPathService {

    LearningPathResponse createLearningPath(Long spaceId, CreateLearningPathRequest request);

    List<LearningPathResponse> getLearningPath(Long spaceId);

    LearningPath getLearningPathEntity(Long id);

    LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId);

    LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request);

    void archiveLearningPath(Long spaceId, Long pathId);

    void restoreLearningPath(Long spaceId, Long pathId);

    void deleteLearningPath(Long spaceId, Long pathId);

    void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request);
}
