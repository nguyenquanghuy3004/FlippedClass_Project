package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.ReviewRequest;
import com.example.flippedclass.dto.request.activity.StudyGroupCreateRequest;
import com.example.flippedclass.dto.response.activity.AvailableGroupResponse;
import com.example.flippedclass.dto.response.activity.StudyGroupDetailResponse;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.enums.GroupStatus;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.service.StudyGroupService;
import com.example.flippedclass.validation.GroupActivityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository groupRepository;
    private final GroupActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final StudyGroupMemberRepository memberRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;
    private final GroupActivityValidator validator;

    @Override
    @Transactional(readOnly = true)
    public List<AvailableGroupResponse> getAvailableGroups(Long activityId) {
        return groupRepository.findAvailableGroups(activityId).stream()
                .map(group -> AvailableGroupResponse.builder()
                        .id(group.getId())
                        .groupName(group.getGroupName())
                        .currentMembers(group.getMembers().size())
                        .maxMembers(group.getActivity().getMaxMembers())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyGroupDetailResponse createGroup(Long activityId, Long currentUserId, StudyGroupCreateRequest request) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Validate
        validator.validateActivityOpen(activity);
        validator.validateUserNotInAnyGroup(currentUserId, activityId);

        StudyGroup group = StudyGroup.builder()
                .activity(activity)
                .learningSpace(activity.getLearningSpace())
                .groupName(request.getGroupName())
                .inviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(GroupStatus.FORMING)
                .leader(user)
                .build();

        group = groupRepository.save(group);

        StudyGroupMember member = StudyGroupMember.builder()
                .group(group)
                .student(user)
                .role(GroupRole.LEADER)
                .joinedAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);

        return mapToDetailResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyGroupDetailResponse getMyGroup(Long activityId, Long currentUserId) {
        StudyGroup group = groupRepository.findByActivityIdAndStudentId(activityId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Group not found for this user"));
        return mapToDetailResponse(group);
    }

    @Override
    @Transactional
    public void reviewGroup(Long groupId, ReviewRequest request) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        group.setStatus(GroupStatus.REVIEWED);
        
        ActivitySubmission submission = group.getSubmission();
        if (submission != null) {
            submission.setScore(request.getScore());
            submission.setFeedback(request.getFeedback());
            submission.setStatus(SubmissionStatus.GRADED);
        }

        groupRepository.save(group);
    }

    private StudyGroupDetailResponse mapToDetailResponse(StudyGroup group) {
        List<StudyGroupDetailResponse.MemberInfo> memberInfos = group.getMembers().stream()
                .map(m -> StudyGroupDetailResponse.MemberInfo.builder()
                        .userId(m.getStudent().getId())
                        .fullName(m.getStudent().getFullName())
                        .role(m.getRole().name())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        StudyGroupDetailResponse.SubmissionInfo submissionInfo = null;
        if (group.getSubmission() != null) {
            submissionInfo = StudyGroupDetailResponse.SubmissionInfo.builder()
                    .githubRepoUrl(group.getSubmission().getGithubRepoUrl())
                    .submittedAt(group.getSubmission().getSubmittedAt())
                    .score(group.getSubmission().getScore())
                    .feedback(group.getSubmission().getFeedback())
                    .status(group.getSubmission().getStatus())
                    .build();
        }

        return StudyGroupDetailResponse.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .inviteCode(group.getInviteCode())
                .leaderId(group.getLeader().getId())
                .members(memberInfos)
                .submission(submissionInfo)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.example.flippedclass.dto.response.activity.AvailableStudentResponse> getAvailableStudents(Long activityId, Long currentUserId, String keyword) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        
        Long spaceId = activity.getLearningSpace().getId();
        
        List<User> users = learningSpaceMemberRepository.findAvailableStudentsForActivity(spaceId, activityId, keyword);
        
        return users.stream()
                .filter(u -> !u.getId().equals(currentUserId)) // Optional: exclude current user if they are searching (though usually leader is already in group, so this is just extra safety)
                .map(u -> com.example.flippedclass.dto.response.activity.AvailableStudentResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .build())
                .collect(Collectors.toList());
    }
}
