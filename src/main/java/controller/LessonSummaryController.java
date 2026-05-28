package controller;

import dto.request.FeedbackSummaryRequest;
import dto.request.SubmitSummaryRequest;
import dto.response.LessonSummaryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.LessonSummaryService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/summaries")
public class LessonSummaryController {

    private final LessonSummaryService lessonSummaryService;

    public LessonSummaryController(LessonSummaryService lessonSummaryService) {
        this.lessonSummaryService = lessonSummaryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonSummaryResponse submitSummary(@Valid @RequestBody SubmitSummaryRequest request) {
        return lessonSummaryService.submitSummary(request);
    }

    @PutMapping("/{id}/feedback")
    public LessonSummaryResponse provideFeedback(
            @PathVariable("id") @Positive(message = "id must be a positive number") Long id,
            @Valid @RequestBody FeedbackSummaryRequest request) {
        return lessonSummaryService.provideFeedback(id, request);
    }

    @GetMapping("/learning-node/{nodeId}")
    public List<LessonSummaryResponse> getSummariesByLearningNode(
            @PathVariable("nodeId") @Positive(message = "nodeId must be a positive number") Long nodeId) {
        return lessonSummaryService.getSummariesByLearningNode(nodeId);
    }

    @GetMapping("/student/{studentId}")
    public List<LessonSummaryResponse> getSummariesByStudent(
            @PathVariable("studentId") @Positive(message = "studentId must be a positive number") Long studentId) {
        return lessonSummaryService.getSummariesByStudent(studentId);
    }
}
