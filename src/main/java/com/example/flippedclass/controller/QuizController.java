package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.flippedclass.service.QuizService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Management", description = "CRUD operations for quizzes")
@SecurityRequirement(name = "bearerAuth")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Create a new quiz")
    public QuizResponse create(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody CreateQuizRequest request) {
        return quizService.createForCurrentUser(currentUser.getId(), request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Update an existing quiz")
    public QuizResponse update(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("id") @Positive(message = "id must be a positive number") Long id,
            @Valid @RequestBody UpdateQuizRequest request) {
        return quizService.updateOwned(currentUser.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Delete a quiz")
    public void delete(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        quizService.deleteOwned(currentUser.getId(), id);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Get current lecturer's quizzes")
    public List<QuizResponse> getMyQuizzes(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return quizService.getByLecturer(currentUser.getId());
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Add a question to a quiz")
    public QuizQuestionResponse addQuestion(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
            @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.addQuestionOwned(currentUser.getId(), quizId, request);
    }

    @PutMapping("/questions/{id}")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Update a question")
    public QuizQuestionResponse updateQuestion(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("id") @Positive(message = "id must be a positive number") Long id,
            @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.updateQuestionOwned(currentUser.getId(), id, request);
    }

    @DeleteMapping("/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Delete a question")
    public void deleteQuestion(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("questionId") @Positive(message = "questionId must be a positive number") Long questionId) {
        quizService.deleteQuestionOwned(currentUser.getId(), questionId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz details by ID")
    public QuizResponse getById(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        return quizService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all quizzes")
    public List<QuizResponse> getAll() {
        return quizService.getAll();
    }

    @GetMapping("/lecturer/{lecturerId}")
    @Operation(summary = "Get quizzes by lecturer ID")
    public List<QuizResponse> getByLecturer(
            @PathVariable("lecturerId") @Positive(message = "lecturerId must be a positive number") Long lecturerId) {
        return quizService.getByLecturer(lecturerId);
    }

    @GetMapping("/{quizId}/questions")
    @Operation(summary = "Get questions for a quiz")
    public List<QuizQuestionResponse> getQuestions(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        
        List<QuizQuestionResponse> questions = quizService.getQuestions(quizId);
        
        // Security Patch: Only MENTORs and ADMINs can see the correct answers.
        // If unauthenticated or STUDENT, strip the correct answers.
        boolean isMentorOrAdmin = currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MENTOR") || a.getAuthority().equals("ROLE_MENTOR")
                        || a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isMentorOrAdmin) {
            questions.forEach(q -> q.setCorrectAnswer(null));
        }
        
        return questions;
    }

    @GetMapping("/learning-node/{nodeId}/active")
    @Operation(summary = "Get active quizzes for a learning node")
    public List<QuizResponse> getActiveQuizzesByLearningNode(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId) {
        return quizService.getActiveQuizzesByLearningNode(nodeId);
    }

    @GetMapping("/{quizId}/statistics")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Get quiz statistics")
    public QuizStatisticsResponse getStatistics(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getStatistics(quizId);
    }

    @com.example.flippedclass.annotation.LogUserActivity(actionType = "SUBMIT_QUIZ", description = "'Đã nộp bài Quiz ID: ' + #quizId")
    @PostMapping("/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a quiz attempt")
    public QuizAttemptResponse submitAttempt(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
            @Valid @RequestBody SubmitQuizAttemptRequest request) {
        return quizService.submitAttempt(quizId, request);
    }

    @GetMapping("/{quizId}/attempts")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @Operation(summary = "Get all attempts for a quiz")
    public List<QuizAttemptResponse> getAttempts(
            @PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getAttempts(quizId);
    }

    @GetMapping("/attempts/student/{studentId}")
    @Operation(summary = "Get all attempts by a student")
    public List<QuizAttemptResponse> getAttemptsByStudent(
            @PathVariable("studentId") @Positive(message = "studentId must be a positive number") Long studentId) {
        return quizService.getAttemptsByStudent(studentId);
    }

    @GetMapping("/attempts/{id}")
    @Operation(summary = "Get a specific attempt by ID")
    public QuizAttemptResponse getAttemptById(
            @PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        return quizService.getAttemptById(id);
    }
}
