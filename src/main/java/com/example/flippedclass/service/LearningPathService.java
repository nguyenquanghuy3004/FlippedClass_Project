package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LearningPathService {

    LearningPath getLearningPathEntity(Long id);

    LearningPathResponse createLearningPath(Long spaceId, CreateLearningPathRequest request);

    List<LearningPathResponse> getLearningPath(Long spaceId, Long studentId);

    LearningPathResponse getLearningPathDetail(Long spaceId, Long pathId, Long studentId);

    LearningPathResponse updateLearningPath(Long spaceId, Long pathId, UpdateLearningPathRequest request);

    void archiveLearningPath(Long spaceId, Long pathId);

    void restoreLearningPath(Long spaceId, Long pathId);

    void deleteLearningPathModul(Long spaceId, Long pathId);

    void deleteAllLearningPaths(Long spaceId);

//    void reorderLearningPaths(Long spaceId, ReorderLearningPathRequest request);

    List<LearningPathResponse> getDeletedLearningPaths(Long spaceId);
}
