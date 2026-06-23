package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.GroupCreateRequest;
import com.example.flippedclass.dto.request.activity.GroupReviewRequest;
import com.example.flippedclass.dto.response.activity.GroupDetailResponse;
import com.example.flippedclass.dto.response.activity.GroupResponse;
import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.entity.ActivityGroupMember;
import com.example.flippedclass.entity.ClassroomActivity;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.enums.GroupStatus;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.IllegalActivityStateException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivityGroupMemberRepository;
import com.example.flippedclass.repository.ActivityGroupRepository;
import com.example.flippedclass.repository.ClassroomActivityRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final ActivityGroupRepository groupRepository;
    private final ClassroomActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityGroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public GroupDetailResponse createGroup(Long activityId, Long userId, GroupCreateRequest request) {
        ClassroomActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        if (activity.getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("ACTIVITY_NOT_OPEN", "Can only create groups when activity is OPEN");
        }

        // Must lock or rely on DB unique constraints to prevent race conditions.
        boolean alreadyInGroup = groupMemberRepository.existsByActivityIdAndUserIdWithLock(activityId, userId);
        if (alreadyInGroup) {
            throw new BusinessException("ALREADY_IN_GROUP", "User is already in a group for this activity");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ActivityGroup group = ActivityGroup.builder()
                .activity(activity)
                .groupName(request.getGroupName())
                .status(GroupStatus.FORMING)
                .build();
        
        group = groupRepository.save(group);

        ActivityGroupMember leader = ActivityGroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.LEADER)
                .joinedAt(LocalDateTime.now())
                .build();
        
        groupMemberRepository.save(leader);

        return getGroupDetail(group.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAvailableGroups(Long activityId) {
        return groupRepository.findAvailableGroups(activityId, GroupStatus.FORMING).stream()
                .map(g -> GroupResponse.builder()
                        .id(g.getId())
                        .groupName(g.getGroupName())
                        .status(g.getStatus())
                        .currentMembers(g.getMembers().size())
                        .maxMembers(g.getActivity().getMaxMembersPerGroup())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDetailResponse> getAllGroupsByActivity(Long activityId) {
        return groupRepository.findByActivity_Id(activityId).stream()
                .map(g -> getGroupDetail(g.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDetailResponse getGroupDetail(Long groupId) {
        ActivityGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));
        
        GroupDetailResponse response = GroupDetailResponse.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .finalGrade(group.getFinalGrade())
                .gradeComment(group.getGradeComment())
                .gradedAt(group.getGradedAt())
                .status(group.getStatus())
                .build();
        // Add mappers for members and submission here in real impl
        return response;
    }

    @Override
    @Transactional
    public void reviewGroup(Long groupId, GroupReviewRequest request) {
        ActivityGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        if (group.getActivity().getStatus() != ActivityStatus.LOCKED) {
            throw new IllegalActivityStateException("Can only grade when activity is LOCKED");
        }

        group.setFinalGrade(request.getScore());
        group.setGradeComment(request.getFeedback());
        group.setGradedAt(LocalDateTime.now());
        group.setStatus(GroupStatus.REVIEWED);

        if (group.getSubmission() != null) {
            group.getSubmission().setStatus(SubmissionStatus.GRADED);
        }

        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteGroupIfEmpty(Long groupId) {
        ActivityGroup group = groupRepository.findById(groupId).orElse(null);
        if (group != null && group.getMembers().isEmpty()) {
            groupRepository.delete(group);
        }
    }
}
