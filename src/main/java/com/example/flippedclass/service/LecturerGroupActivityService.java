package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;

import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;

import java.util.List;

public interface LecturerGroupActivityService {
    LecturerActivityResponse createActivity(Long currentUserId, LecturerActivityCreateRequest request);
    List<LecturerActivityResponse> listActivitiesBySpaceId(Long spaceId);
    LecturerActivityResponse getActivityDetails(Long activityId);
    List<LecturerGroupResponse> listGroupsByActivityId(Long activityId, String search, String filter);
}
