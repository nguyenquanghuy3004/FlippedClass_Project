package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.SubmitSummaryRequest;
import com.example.flippedclass.dto.response.LessonSummaryResponse;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import com.example.flippedclass.service.LessonSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson-summaries")
@RequiredArgsConstructor
public class LessonSummaryController {

    private final LessonSummaryService lessonSummaryService;

    @PostMapping
    public ResponseEntity<LessonSummaryResponse> submitSummary(
            @Valid @RequestBody SubmitSummaryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Ensure student ID is set from the authenticated user
        request.setStudentId(userDetails.getId());
        
        LessonSummaryResponse response = lessonSummaryService.submitSummary(request);
        return ResponseEntity.ok(response);
    }
}
