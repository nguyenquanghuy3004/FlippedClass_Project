package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerSubmissionResponse;
import com.example.flippedclass.entity.ActivitySubmission;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivitySubmissionRepository;
import com.example.flippedclass.service.LecturerSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LecturerSubmissionServiceImpl implements LecturerSubmissionService {

    private final ActivitySubmissionRepository submissionRepository;

    @Override
    @Transactional(readOnly = true)
    public LecturerSubmissionResponse getSubmissionDetails(Long submissionId) {
        ActivitySubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));

        return mapToResponse(submission);
    }

    @Override
    @Transactional
    public LecturerSubmissionResponse gradeSubmission(Long submissionId, LecturerGradeRequest request) {
        ActivitySubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);

        submission = submissionRepository.save(submission);
        return mapToResponse(submission);
    }

    private LecturerSubmissionResponse mapToResponse(ActivitySubmission submission) {
        return LecturerSubmissionResponse.builder()
                .id(submission.getId())
                .groupName(submission.getGroup() != null ? submission.getGroup().getGroupName() : "Unknown")
                .githubUrl(submission.getGithubRepoUrl())
                .submittedBy(submission.getSubmittedBy() != null ? submission.getSubmittedBy().getFullName() : "Unknown")
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .build();
    }
}
