package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.DiscussionRequest;
import com.example.flippedclass.dto.response.DiscussionResponse;
import com.example.flippedclass.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes/{nodeId}/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    @GetMapping
    public ResponseEntity<List<DiscussionResponse>> getDiscussions(
            @PathVariable Long nodeId,
            @RequestParam(required = false) Long groupId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByNodeId(nodeId, groupId));
    }

    @PostMapping
    public ResponseEntity<DiscussionResponse> addDiscussion(
            @PathVariable Long nodeId,
            @RequestParam(required = false) Long groupId,
            @RequestBody DiscussionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(discussionService.addDiscussion(nodeId, groupId, authentication.getName(), request));
    }

    @PutMapping("/{discussionId}/solved")
    public ResponseEntity<DiscussionResponse> toggleSolved(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        return ResponseEntity.ok(discussionService.markAsSolved(discussionId, authentication.getName()));
    }

    @PutMapping("/{discussionId}/pin")
    public ResponseEntity<DiscussionResponse> togglePin(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        return ResponseEntity.ok(discussionService.togglePin(discussionId, authentication.getName()));
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        discussionService.deleteDiscussion(discussionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
