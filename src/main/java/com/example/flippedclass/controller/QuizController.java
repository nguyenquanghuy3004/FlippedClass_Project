package com.example.flippedclass.controller;

import com.example.flippedclass.dto.QuizResponse;
import com.example.flippedclass.service.QuizService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/learning-nodes/{nodeId}/quizzes")
    public List<QuizResponse> findByLearningNode(@PathVariable Long nodeId) {
        return quizService.findByLearningNode(nodeId);
    }
}
