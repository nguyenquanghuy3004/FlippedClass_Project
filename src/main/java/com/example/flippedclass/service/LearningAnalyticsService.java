package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.CompletedNodeResponse;
import com.example.flippedclass.dto.response.SpaceAnalyticsDTO;

public interface LearningAnalyticsService {
    SpaceAnalyticsDTO getSpaceAnalytics(Long spaceId);

    java.util.List<CompletedNodeResponse> getCompletedNodesDetail(Long spaceId, Long studentId);
}
