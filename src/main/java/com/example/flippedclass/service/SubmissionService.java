package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;

public interface SubmissionService {
    void submitWork(Long groupId, Long currentUserId, SubmissionRequest request);
}
