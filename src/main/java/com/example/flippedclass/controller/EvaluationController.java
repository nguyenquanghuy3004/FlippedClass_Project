package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.*;
import com.example.flippedclass.dto.request.CreateEvaluationSessionRequest;
import com.example.flippedclass.dto.request.CreateInteractionLogRequest;
import com.example.flippedclass.dto.request.SubmitGradeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.flippedclass.service.EvaluationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationSessionResponse createSession(@Valid @RequestBody CreateEvaluationSessionRequest request) {
        return evaluationService.createSession(request);
    }

    @GetMapping("/sessions")
    public List<EvaluationSessionResponse> getSessionsByLecturer(
            @RequestParam @Positive(message = "lecturerId must be a positive number") Long lecturerId) {
        return evaluationService.getSessionsByLecturer(lecturerId);
    }

    @GetMapping("/learning-paths")
    public List<LearningPathResponse> getLearningPathsForLecturer(
            @RequestParam @Positive(message = "lecturerId must be a positive number") Long lecturerId) {
        return evaluationService.getLearningPathsForLecturer(lecturerId);
    }

    @GetMapping("/sessions/{sessionId}/students")
    public List<UserResponse> getStudentsForSession(
            @PathVariable @Positive(message = "sessionId must be a positive number") Long sessionId) {
        return evaluationService.getStudentsForSession(sessionId);
    }

    @GetMapping("/sessions/{sessionId}/grading-context")
    public GradingContextResponse getGradingContext(
            @PathVariable @Positive(message = "sessionId must be a positive number") Long sessionId,
            @RequestParam @Positive(message = "studentId must be a positive number") Long studentId) {
        return evaluationService.getGradingContext(sessionId, studentId);
    }

    @GetMapping("/students/{studentId}/interactions")
    public List<InteractionLogResponse> getInteractionHistory(
            @PathVariable @Positive(message = "studentId must be a positive number") Long studentId,
            @RequestParam(required = false) @Positive(message = "learningPathId must be a positive number") Long learningPathId) {
        return evaluationService.getInteractionHistory(studentId, learningPathId);
    }

    @PostMapping("/grades")
    @ResponseStatus(HttpStatus.CREATED)
    public GradeEntryResponse submitGrade(@Valid @RequestBody SubmitGradeRequest request) {
        return evaluationService.submitGrade(request);
    }

    @GetMapping("/sessions/{sessionId}/students/{studentId}/grades")
    public List<GradeEntryResponse> getGrades(
            @PathVariable @Positive(message = "sessionId must be a positive number") Long sessionId,
            @PathVariable @Positive(message = "studentId must be a positive number") Long studentId) {
        return evaluationService.getGradesForStudentInSession(sessionId, studentId);
    }

    @GetMapping("/students/{studentId}/grades")
    public List<GradeEntryResponse> getGradesForStudent(
            @PathVariable @Positive(message = "studentId must be a positive number") Long studentId) {
        return evaluationService.getGradesForStudent(studentId);
    }

}
