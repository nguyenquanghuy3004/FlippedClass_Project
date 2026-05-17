package controller;

import dto.response.*;
import dto.request.CreateEvaluationSessionRequest;
import dto.request.CreateInteractionLogRequest;
import dto.request.SubmitGradeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.EvaluationService;

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

    @GetMapping("/sessions/{id}")
    public EvaluationSessionResponse getSession(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        return evaluationService.getSession(id);
    }

    @GetMapping("/sessions")
    public List<EvaluationSessionResponse> getSessionsByLecturer(
            @RequestParam @Positive(message = "lecturerId must be a positive number") Long lecturerId) {
        return evaluationService.getSessionsByLecturer(lecturerId);
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
            @RequestParam(required = false)
            @Size(min = 2, max = 100, message = "courseName must be between 2 and 100 characters") String courseName) {
        return evaluationService.getInteractionHistory(studentId, courseName);
    }

    @PostMapping("/interactions")
    @ResponseStatus(HttpStatus.CREATED)
    public InteractionLogResponse addInteractionLog(@Valid @RequestBody CreateInteractionLogRequest request) {
        return evaluationService.addInteractionLog(request);
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

    @GetMapping("/students/{studentId}/profile")
    public StudentProfileResponse getStudentProfile(
            @PathVariable @Positive(message = "studentId must be a positive number") Long studentId) {
        return evaluationService.getStudentProfile(studentId);
    }
}
