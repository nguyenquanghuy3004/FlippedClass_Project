package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.dto.response.activity.SubmissionResponse;
import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.entity.ActivityGroupMember;
import com.example.flippedclass.entity.ActivitySubmission;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.event.SubmissionCreatedEvent;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivityGroupMemberRepository;
import com.example.flippedclass.repository.ActivityGroupRepository;
import com.example.flippedclass.repository.ActivitySubmissionRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.SubmissionService;
import com.example.flippedclass.validation.SubmissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final ActivitySubmissionRepository submissionRepository;
    private final ActivityGroupRepository groupRepository;
    private final ActivityGroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final SubmissionValidator submissionValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SubmissionResponse submitWork(Long groupId, Long userId, SubmissionRequest request) {
        ActivityGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        ActivityGroupMember member = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new BusinessException("NOT_IN_GROUP", "User is not in this group"));

        if (member.getRole() != GroupRole.LEADER) {
            throw new BusinessException("ONLY_LEADER_CAN_SUBMIT", "Only the group leader can submit the work");
        }

        submissionValidator.validateForSubmission(request, group);

        User user = userRepository.findById(userId).orElseThrow();

        ActivitySubmission submission = submissionRepository.findByGroup_Id(groupId)
                .orElse(new ActivitySubmission());

        submission.setGroup(group);
        submission.setGithubUrl(request.getGithubUrl());
        submission.setNote(request.getNote());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setSubmittedBy(user);
        
        boolean isLate = LocalDateTime.now().isAfter(group.getActivity().getDeadline());
        submission.setStatus(isLate ? SubmissionStatus.LATE_SUBMITTED : SubmissionStatus.SUBMITTED);

        submission = submissionRepository.save(submission);

        eventPublisher.publishEvent(new SubmissionCreatedEvent(this, submission.getId(), groupId));

        return mapToResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionByGroup(Long groupId) {
        return submissionRepository.findByGroup_Id(groupId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Submission not found for group"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getAllSubmissionsByActivity(Long activityId) {
        return submissionRepository.findByGroup_Activity_Id(activityId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubmissionResponse mapToResponse(ActivitySubmission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .groupId(submission.getGroup().getId())
                .githubUrl(submission.getGithubUrl())
                .note(submission.getNote())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus())
                .submittedById(submission.getSubmittedBy().getId())
                .submittedByFullName(submission.getSubmittedBy().getFullName())
                .build();
    }
}
