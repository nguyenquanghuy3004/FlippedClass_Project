package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.DiscussionRequest;
import com.example.flippedclass.dto.response.DiscussionResponse;

import java.util.List;

public interface DiscussionService {
    List<DiscussionResponse> getDiscussionsByNodeId(Long nodeId);
    DiscussionResponse addDiscussion(Long nodeId, String username, DiscussionRequest request);
    DiscussionResponse markAsSolved(Long discussionId, String username);
    DiscussionResponse togglePin(Long discussionId, String username);
    void deleteDiscussion(Long discussionId, String username);
}
