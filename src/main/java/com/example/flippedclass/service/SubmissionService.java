package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.dto.response.activity.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse submitWork(Long groupId, Long userId, SubmissionRequest request);
    SubmissionResponse getSubmissionByGroup(Long groupId);
    List<SubmissionResponse> getAllSubmissionsByActivity(Long activityId);
}
