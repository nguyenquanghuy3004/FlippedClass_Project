package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.entity.ActivitySubmission;
import com.example.flippedclass.entity.StudyGroup;
import com.example.flippedclass.enums.GroupStatus;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivitySubmissionRepository;
import com.example.flippedclass.repository.StudyGroupRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.SubmissionService;
import com.example.flippedclass.validation.GroupActivityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final StudyGroupRepository groupRepository;
    private final ActivitySubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final GroupActivityValidator validator;

    @Override
    @Transactional
    public void submitWork(Long groupId, Long currentUserId, SubmissionRequest request) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        validator.validateLeaderRole(currentUserId, group);
        validator.validateActivityOpen(group.getActivity());
        validator.validateDeadline(group.getActivity());

        ActivitySubmission submission = group.getSubmission();
        if (submission == null) {
            submission = ActivitySubmission.builder()
                    .group(group)
                    .githubRepoUrl(request.getGithubRepoUrl())
                    .note(request.getNote())
                    .submittedAt(LocalDateTime.now())
                    .status(SubmissionStatus.SUBMITTED)
                    .submittedBy(userRepository.findById(currentUserId).orElseThrow())
                    .build();
            group.setSubmission(submission);
        } else {
            submission.setGithubRepoUrl(request.getGithubRepoUrl());
            submission.setNote(request.getNote());
            submission.setSubmittedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }

        group.setStatus(GroupStatus.SUBMITTED);

        submissionRepository.save(submission);
        groupRepository.save(group);
    }
}
