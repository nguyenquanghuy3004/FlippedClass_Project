package com.example.flippedclass.service;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.res.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.res.LearningSpaceResponse;

public interface LearningSpaceService {

    LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request);

    JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request);

    void deleteLearningSpace(Long id);

    void restoreLearningSpace(Long id);
}
