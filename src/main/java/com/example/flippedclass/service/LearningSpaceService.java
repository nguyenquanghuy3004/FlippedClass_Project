package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.response.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.response.LearningSpaceResponse;
import com.example.flippedclass.dto.response.SpacePreviewResponse;
import com.example.flippedclass.entity.LearningSpace;
import jakarta.transaction.Transactional;
import java.util.List;

public interface LearningSpaceService {

    LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request);

    List<LearningSpaceResponse> getMySpaces();


    @Transactional
    LearningSpace updateLearningSpace(Long id, LearningSpace spaceDetail);

    JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request);

    SpacePreviewResponse previewLearningSpace(String inviteCode);

    void deleteLearningSpace(Long id);

    void archiveLearningSpace(Long id);

    void restoreLearningSpace(Long id);

}
