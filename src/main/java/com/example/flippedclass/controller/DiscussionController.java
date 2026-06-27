package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.DiscussionRequest;
import com.example.flippedclass.dto.response.DiscussionResponse;
import com.example.flippedclass.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nodes/{nodeId}/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<DiscussionResponse>> getDiscussions(@PathVariable Long nodeId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByNodeId(nodeId));
    }

    @PostMapping
    public ResponseEntity<DiscussionResponse> addDiscussion( @PathVariable Long nodeId,@RequestBody DiscussionRequest request,Authentication authentication) {
        DiscussionResponse response = discussionService.addDiscussion(nodeId, authentication.getName(), request);
        messagingTemplate.convertAndSend("/topic/nodes/" + nodeId + "/discussions", (Object) Map.of("action", "UPDATE"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{discussionId}/solved")
    public ResponseEntity<DiscussionResponse> toggleSolved(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        DiscussionResponse response = discussionService.markAsSolved(discussionId, authentication.getName());
        messagingTemplate.convertAndSend("/topic/nodes/" + nodeId + "/discussions", (Object) Map.of("action", "UPDATE"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{discussionId}/pin")
    public ResponseEntity<DiscussionResponse> togglePin(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        DiscussionResponse response = discussionService.togglePin(discussionId, authentication.getName());
        messagingTemplate.convertAndSend("/topic/nodes/" + nodeId + "/discussions", (Object) Map.of("action", "UPDATE"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable Long nodeId,
            @PathVariable Long discussionId,
            Authentication authentication) {
        discussionService.deleteDiscussion(discussionId, authentication.getName());
        messagingTemplate.convertAndSend("/topic/nodes/" + nodeId + "/discussions", (Object) Map.of("action", "UPDATE"));
        return ResponseEntity.noContent().build();
    }
}
