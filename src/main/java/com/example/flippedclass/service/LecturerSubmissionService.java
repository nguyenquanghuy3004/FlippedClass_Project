package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerSubmissionResponse;

public interface LecturerSubmissionService {
    LecturerSubmissionResponse getSubmissionDetails(Long submissionId);
    LecturerSubmissionResponse gradeSubmission(Long submissionId, LecturerGradeRequest request);
}
