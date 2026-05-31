package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.flippedclass.service.QuizService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/learning-nodes/{nodeId}/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse create(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
            @Valid @RequestBody CreateQuizRequest request
    ) {
        return quizService.create(nodeId, request);
    }

    @PutMapping("/{quizId}")
    public QuizResponse update(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                               @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                               @Valid @RequestBody UpdateQuizRequest request) {
        return quizService.update(nodeId, quizId, request);
    }

    @GetMapping("/{quizId}")
    public QuizResponse getById(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                                @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getById(nodeId, quizId);
    }

    @GetMapping
    public List<QuizResponse> findByLearningNode(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId) {
        return quizService.findByLearningNode(nodeId);
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                       @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        quizService.delete(nodeId, quizId);
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizQuestionResponse addQuestion(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                                            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                                            @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.addQuestion(nodeId, quizId, request);
    }

    @GetMapping("/{quizId}/questions")
    public List<QuizQuestionResponse> getQuestions(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                                                   @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getQuestions(nodeId, quizId);
    }

    @GetMapping("/{quizId}/attempts")
    public List<QuizAttemptResponse> getAttempts(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                                                 @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getAttempts(nodeId, quizId);
    }

    @GetMapping("/{quizId}/statistics")
    public QuizStatisticsResponse getStatistics(@PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId,
                                                @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getStatistics(nodeId, quizId);
    }
}
