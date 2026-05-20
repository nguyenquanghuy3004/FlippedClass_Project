package com.example.flippedclass.service;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.res.LearningSpaceResponse;

public interface LearningSpaceService {
    LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request);
    void deleteLearningSpace(Long id);
    void restoreLearningSpace(Long id);
}
