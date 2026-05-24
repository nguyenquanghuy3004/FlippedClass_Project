package com.example.flippedclass.service;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.req.UpdateLearningSpaceRequest;
import com.example.flippedclass.dto.res.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.res.LearningSpaceResponse;
import com.example.flippedclass.entity.LearningSpace;
import jakarta.transaction.Transactional;

public interface LearningSpaceService {

    LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request);

    // update -------------------------
    @Transactional
    LearningSpace updateLearningSpace(Long id, LearningSpace spaceDetail);

    JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request);

    void deleteLearningSpace(Long id);

    void restoreLearningSpace(Long id);

}
