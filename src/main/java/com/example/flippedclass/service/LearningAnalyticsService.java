package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.CompletedNodeResponse;
import com.example.flippedclass.dto.response.SpaceAnalyticsDTO;

import java.util.List;

public interface LearningAnalyticsService {

    SpaceAnalyticsDTO getSpaceAnalytics(Long spaceId);
   List<CompletedNodeResponse> getCompletedNodesDetail(Long spaceId, Long studentId);
}
