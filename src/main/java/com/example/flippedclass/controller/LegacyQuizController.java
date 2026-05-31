package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class LegacyQuizController {

    private final QuizService quizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse create(@Valid @RequestBody CreateQuizRequest request) {
        return quizService.create(request);
    }

    @PutMapping("/{id}")
    public QuizResponse update(@PathVariable("id") @Positive(message = "id must be a positive number") Long id,
                               @Valid @RequestBody UpdateQuizRequest request) {
        return quizService.update(id, request);
    }

    @GetMapping("/{id}")
    public QuizResponse getById(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        return quizService.getById(id);
    }

    @GetMapping
    public List<QuizResponse> getAll() {
        return quizService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        quizService.delete(id);
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizQuestionResponse addQuestion(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
            @Valid @RequestBody CreateQuizQuestionRequest request
    ) {
        return quizService.addQuestion(quizId, request);
    }

    @GetMapping("/{quizId}/questions")
    public List<QuizQuestionResponse> getQuestions(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId
    ) {
        return quizService.getQuestions(quizId);
    }

    @DeleteMapping("/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(
            @PathVariable("questionId") @Positive(message = "questionId must be a positive number") Long questionId
    ) {
        quizService.deleteQuestion(questionId);
    }

    @PostMapping("/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptResponse submitAttempt(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
            @Valid @RequestBody SubmitQuizAttemptRequest request
    ) {
        return quizService.submitAttempt(quizId, request);
    }

    @GetMapping("/{quizId}/attempts")
    public List<QuizAttemptResponse> getAttempts(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId
    ) {
        return quizService.getAttempts(quizId);
    }

    @GetMapping("/{quizId}/statistics")
    public QuizStatisticsResponse getStatistics(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId
    ) {
        return quizService.getStatistics(quizId);
    }

    @GetMapping("/learning-node/{nodeId}/active")
    public List<QuizResponse> getActiveQuizzesByLearningNode(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId
    ) {
        return quizService.getActiveQuizzesByLearningNode(nodeId);
    }
}
