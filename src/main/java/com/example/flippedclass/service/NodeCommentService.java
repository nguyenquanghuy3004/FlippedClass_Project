package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateNodeCommentRequest;
import com.example.flippedclass.dto.response.NodeCommentResponse;

import java.util.List;

public interface NodeCommentService {
    List<NodeCommentResponse> getCommentsByNodeId(Long nodeId, Long userId);
    NodeCommentResponse createComment(Long nodeId, Long userId, CreateNodeCommentRequest request);
    NodeCommentResponse replyToComment(Long nodeId, Long parentCommentId, Long userId, CreateNodeCommentRequest request);
    NodeCommentResponse editComment(Long commentId, Long userId, CreateNodeCommentRequest request);
    void deleteOwnComment(Long commentId, Long userId);
}
