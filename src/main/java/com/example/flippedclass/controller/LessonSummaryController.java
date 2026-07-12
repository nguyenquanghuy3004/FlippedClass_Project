package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.FeedbackSummaryRequest;
import com.example.flippedclass.dto.request.SubmitSummaryRequest;
import com.example.flippedclass.dto.response.LessonSummaryResponse;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import com.example.flippedclass.service.LessonSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-summaries")
@RequiredArgsConstructor
public class LessonSummaryController {

    private final LessonSummaryService lessonSummaryService;
    private NotificationController notifController;

    @PostMapping
    public ResponseEntity<LessonSummaryResponse> submitSummary(
            @Valid @RequestBody SubmitSummaryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Ensure student ID is set from the authenticated user
        request.setStudentId(userDetails.getId());
        
        LessonSummaryResponse response = lessonSummaryService.submitSummary(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/learning-node/{nodeId}")
    public ResponseEntity<List<LessonSummaryResponse>> getSummariesByNode(@PathVariable Long nodeId) {
        return ResponseEntity.ok(lessonSummaryService.getSummariesByLearningNode(nodeId));
    }

    @GetMapping("/learning-node/{nodeId}/my-summary")
    public ResponseEntity<LessonSummaryResponse> getMySummary(@PathVariable Long nodeId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        LessonSummaryResponse response = lessonSummaryService.getMySummaryByNode(nodeId, userDetails.getId());
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonSummaryResponse> getSummaryById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonSummaryService.getSummaryById(id));
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<LessonSummaryResponse> provideFeedback(@PathVariable Long id,@Valid @RequestBody FeedbackSummaryRequest request) {
        return ResponseEntity.ok(lessonSummaryService.provideFeedback(id, request));
    }
}
