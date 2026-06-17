package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateNodeCommentRequest;
import com.example.flippedclass.dto.response.NodeCommentResponse;
import com.example.flippedclass.service.NodeCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NodeCommentController {

    private final NodeCommentService nodeCommentService;

    @GetMapping("/learning-nodes/{nodeId}/comments")
    public ResponseEntity<List<NodeCommentResponse>> getCommentsByNodeId(
            @PathVariable Long nodeId,
            @RequestParam Long userId) {
        
        List<NodeCommentResponse> comments = nodeCommentService.getCommentsByNodeId(nodeId, userId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/learning-nodes/{nodeId}/comments")
    public ResponseEntity<NodeCommentResponse> createComment(
            @PathVariable Long nodeId,
            @RequestParam Long userId,
            @Valid @RequestBody CreateNodeCommentRequest request) {
        
        NodeCommentResponse comment = nodeCommentService.createComment(nodeId, userId, request);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/learning-nodes/{nodeId}/comments/{commentId}/replies")
    public ResponseEntity<NodeCommentResponse> replyToComment(
            @PathVariable Long nodeId,
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @Valid @RequestBody CreateNodeCommentRequest request) {
        
        NodeCommentResponse reply = nodeCommentService.replyToComment(nodeId, commentId, userId, request);
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<NodeCommentResponse> editComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @Valid @RequestBody CreateNodeCommentRequest request) {
        
        NodeCommentResponse comment = nodeCommentService.editComment(commentId, userId, request);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteOwnComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        
        nodeCommentService.deleteOwnComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
