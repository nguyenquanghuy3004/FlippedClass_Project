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
    private final com.example.flippedclass.repository.NotificationRepository notificationRepository;

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
        
        if (submission.getGroup() != null && submission.getGroup().getMembers() != null) {
            String activityTitle = submission.getGroup().getActivity() != null ? submission.getGroup().getActivity().getTitle() : "Activity";
            Long nodeId = (submission.getGroup().getActivity() != null && submission.getGroup().getActivity().getLearningNode() != null) ? submission.getGroup().getActivity().getLearningNode().getId() : null;
            Long spaceId = (submission.getGroup().getActivity() != null && submission.getGroup().getActivity().getLearningSpace() != null) ? submission.getGroup().getActivity().getLearningSpace().getId() : null;
            String targetUrl = "/student/learning-node?nodeId=" + nodeId + "&spaceId=" + spaceId;
            
            for (com.example.flippedclass.entity.StudyGroupMember member : submission.getGroup().getMembers()) {
                com.example.flippedclass.entity.Notification notification = com.example.flippedclass.entity.Notification.builder()
                        .recipient(member.getStudent())
                        .type(com.example.flippedclass.enums.NotificationType.REVIEW_MENTOR)
                        .message("Your submission for '" + activityTitle + "' has been reviewed by a mentor.")
                        .targetUrl(targetUrl)
                        .build();
                notificationRepository.save(notification);
            }
        }
        
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
