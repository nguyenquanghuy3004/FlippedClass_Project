package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import org.springframework.stereotype.Service;

@Service
public interface LearningNodeService {
    LearningNodeResponse createLearningNode(Long pathId, CreateLearningNodeRequest request);
}
