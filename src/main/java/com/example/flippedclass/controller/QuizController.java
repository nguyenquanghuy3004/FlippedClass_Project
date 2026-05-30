package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.service.QuizService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Quản lý các API quiz của node học tập
@RestController
@RequestMapping("/api/learning-nodes/{nodeId}/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // Lấy danh sách quiz theo node học tập
    @GetMapping
    public List<QuizResponse> findByLearningNode(@PathVariable Long nodeId) {
        return quizService.findByLearningNode(nodeId);
    }
}
