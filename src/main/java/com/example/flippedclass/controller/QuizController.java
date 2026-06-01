package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.flippedclass.service.QuizService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }


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

    @GetMapping("/lecturer/{lecturerId}")
    public List<QuizResponse> getByLecturer(@PathVariable("lecturerId") @Positive(message = "lecturerId must be a positive number") Long lecturerId) {
        return quizService.getByLecturer(lecturerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        quizService.delete(id);
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizQuestionResponse addQuestion(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                                            @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.addQuestion(quizId, request);
    }

    @GetMapping("/{quizId}/questions")
    public List<QuizQuestionResponse> getQuestions(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getQuestions(quizId);
    }

    @PutMapping("/questions/{id}")
    public QuizQuestionResponse updateQuestion(@PathVariable("id") @Positive(message = "id must be a positive number") Long id,
                                               @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.updateQuestion(id, request);
    }

    @DeleteMapping("/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable("questionId") @Positive(message = "questionId must be a positive number") Long questionId) {
        quizService.deleteQuestion(questionId);
    }

    @PostMapping("/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptResponse submitAttempt(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                                             @Valid @RequestBody SubmitQuizAttemptRequest request) {
        return quizService.submitAttempt(quizId, request);
    }

    @GetMapping("/{quizId}/attempts")
    public List<QuizAttemptResponse> getAttempts(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getAttempts(quizId);
    }

    @GetMapping("/attempts/student/{studentId}")
    public List<QuizAttemptResponse> getAttemptsByStudent(@PathVariable("studentId") @Positive(message = "studentId must be a positive number") Long studentId) {
        return quizService.getAttemptsByStudent(studentId);
    }

    @GetMapping("/attempts/{id}")
    public QuizAttemptResponse getAttemptById(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        return quizService.getAttemptById(id);
    }

    @GetMapping("/{quizId}/statistics")
    public QuizStatisticsResponse getStatistics(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getStatistics(quizId);
    }

    @GetMapping("/learning-node/{nodeId}/active")
    public List<QuizResponse> getActiveQuizzesByLearningNode(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId) {
        return quizService.getActiveQuizzesByLearningNode(nodeId);
    }
}