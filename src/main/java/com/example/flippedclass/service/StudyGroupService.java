package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.StudyGroupCreateRequest;
import com.example.flippedclass.dto.request.activity.ReviewRequest;
import com.example.flippedclass.dto.response.activity.AvailableGroupResponse;
import com.example.flippedclass.dto.response.activity.AvailableStudentResponse;
import com.example.flippedclass.dto.response.activity.StudyGroupDetailResponse;

import java.util.List;

public interface StudyGroupService {
    List<AvailableGroupResponse> getAvailableGroups(Long activityId);
    StudyGroupDetailResponse createGroup(Long activityId, Long currentUserId, StudyGroupCreateRequest request);
    StudyGroupDetailResponse getMyGroup(Long activityId, Long currentUserId);
    void reviewGroup(Long groupId, ReviewRequest request);
    List<AvailableStudentResponse> getAvailableStudents(Long activityId, Long currentUserId, String keyword);
}
