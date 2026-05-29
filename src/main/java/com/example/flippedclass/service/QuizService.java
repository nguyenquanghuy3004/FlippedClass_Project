package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.QuizResponse;
import java.util.List;

public interface QuizService {

    public List<QuizResponse> findByLearningNode(Long nodeId);
}
