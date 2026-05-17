package com.example.flippedclass.service;

import com.example.flippedclass.dto.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.LearningSpaceResponse;

public interface LearningSpaceService {
    LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request);
}
