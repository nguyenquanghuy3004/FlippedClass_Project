package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.CompletedNodeResponse;
import com.example.flippedclass.dto.response.SpaceAnalyticsDTO;
import com.example.flippedclass.service.LearningAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/analytics")
@RequiredArgsConstructor
public class LearningAnalyticsController {

    private final LearningAnalyticsService learningAnalyticsService;

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping
    public ResponseEntity<SpaceAnalyticsDTO> getSpaceAnalytics(@PathVariable Long spaceId) {
        return ResponseEntity.ok(learningAnalyticsService.getSpaceAnalytics(spaceId));
    }

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping("/students/{studentId}/completed-nodes")
    public ResponseEntity<List<CompletedNodeResponse>> getCompletedNodesDetail(@PathVariable Long spaceId, @PathVariable Long studentId) {
        return ResponseEntity.ok(learningAnalyticsService.getCompletedNodesDetail(spaceId, studentId));
    }
}
