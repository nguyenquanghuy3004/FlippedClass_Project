package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.ActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.ActivityStatusUpdateRequest;
import com.example.flippedclass.dto.request.activity.ActivityUpdateRequest;
import com.example.flippedclass.dto.response.activity.ActivityDetailResponse;
import com.example.flippedclass.dto.response.activity.ActivityResponse;

import java.util.List;

public interface ActivityService {
    ActivityResponse createActivity(Long classroomId, ActivityCreateRequest request);
    ActivityDetailResponse getActivityDetails(Long activityId);
    ActivityResponse updateActivity(Long activityId, ActivityUpdateRequest request);
    ActivityResponse updateActivityStatus(Long activityId, ActivityStatusUpdateRequest request);
    List<ActivityResponse> getActivitiesByClassroom(Long classroomId);
}
