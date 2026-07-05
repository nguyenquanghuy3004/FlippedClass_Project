package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.GroupActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.GroupActivityResponse;

public interface GroupActivityService {
    GroupActivityResponse createActivity(Long learningNodeId, Long currentUserId, GroupActivityCreateRequest request);
    GroupActivityResponse getActivityDetails(Long activityId);
    GroupActivityResponse getActivityByNodeId(Long nodeId);
    void publishActivity(Long activityId);
    void closeActivity(Long activityId);
}
