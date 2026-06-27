package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.LecturerActivityUpdateRequest;
import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;
import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;
import com.example.flippedclass.dto.response.activity.LecturerReviewResponse;

import java.util.List;

public interface LecturerGroupActivityService {
    LecturerActivityResponse createActivity(Long currentUserId, LecturerActivityCreateRequest request);
    List<LecturerActivityResponse> listActivitiesBySpaceId(Long spaceId);
    LecturerActivityResponse getActivityDetails(Long activityId);
    
    LecturerActivityResponse updateActivity(Long lecturerId, Long activityId, LecturerActivityUpdateRequest request);
    LecturerActivityResponse updateActivityStatus(Long lecturerId, Long activityId, String newStatus);

    List<LecturerGroupResponse> listGroupsByActivityId(Long activityId, String search, String filter);
    LecturerReviewResponse getGroupReview(Long groupId);
    LecturerReviewResponse gradeGroup(Long currentUserId, Long groupId, LecturerGradeRequest request);

    void deleteActivity(Long lecturerId, Long activityId);
}
