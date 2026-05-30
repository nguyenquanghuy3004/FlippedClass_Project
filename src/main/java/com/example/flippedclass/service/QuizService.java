package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.QuizResponse;
import java.util.List;

// Xử lý nghiệp vụ quiz của node học tập
public interface QuizService {

    public List<QuizResponse> findByLearningNode(Long nodeId);
}
