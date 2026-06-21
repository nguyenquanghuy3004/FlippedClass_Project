package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.GroupCreateRequest;
import com.example.flippedclass.dto.request.activity.GroupReviewRequest;
import com.example.flippedclass.dto.response.activity.GroupDetailResponse;
import com.example.flippedclass.dto.response.activity.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupDetailResponse createGroup(Long activityId, Long userId, GroupCreateRequest request);
    List<GroupResponse> getAvailableGroups(Long activityId);
    List<GroupDetailResponse> getAllGroupsByActivity(Long activityId);
    GroupDetailResponse getGroupDetail(Long groupId);
    void reviewGroup(Long groupId, GroupReviewRequest request);
    void deleteGroupIfEmpty(Long groupId);
}
