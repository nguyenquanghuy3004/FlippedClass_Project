package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.entity.LearningPath;
import java.util.List;

public interface LearningPathService {

    public List<LearningPathResponse> findByLearningSpace(Long learningSpaceId);

    public LearningPathResponse findById(Long id);

    public LearningPathResponse create(Long learningSpaceId, LearningPathRequest request);

    public LearningPathResponse update(Long id, LearningPathRequest request);

    public void delete(Long id);

    public LearningPath getLearningPath(Long id);
}
