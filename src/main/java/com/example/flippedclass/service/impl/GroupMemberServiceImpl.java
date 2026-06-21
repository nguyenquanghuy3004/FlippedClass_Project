package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.activity.GroupMemberResponse;
import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.entity.ActivityGroupMember;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.enums.GroupStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.GroupFullException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivityGroupMemberRepository;
import com.example.flippedclass.repository.ActivityGroupRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.GroupMemberService;
import com.example.flippedclass.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final ActivityGroupRepository groupRepository;
    private final ActivityGroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;

    @Override
    @Transactional
    public GroupMemberResponse joinGroup(Long activityId, Long groupId, Long userId) {
        ActivityGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        if (!group.getActivity().getId().equals(activityId)) {
            throw new BusinessException("INVALID_REQUEST", "Group does not belong to this activity");
        }

        if (group.getActivity().getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("ACTIVITY_NOT_OPEN", "Can only join groups when activity is OPEN");
        }

        if (group.getStatus() != GroupStatus.FORMING) {
            throw new BusinessException("GROUP_LOCKED", "Group is not accepting new members");
        }

        if (group.getMembers().size() >= group.getActivity().getMaxMembersPerGroup()) {
            throw new GroupFullException();
        }

        boolean alreadyInGroup = groupMemberRepository.existsByActivityIdAndUserIdWithLock(activityId, userId);
        if (alreadyInGroup) {
            throw new BusinessException("ALREADY_IN_GROUP", "User is already in a group for this activity");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ActivityGroupMember member = ActivityGroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        member = groupMemberRepository.save(member);

        // Lock group if full
        if (group.getMembers().size() + 1 >= group.getActivity().getMaxMembersPerGroup()) {
            group.setStatus(GroupStatus.LOCKED);
            groupRepository.save(group);
        }

        return GroupMemberResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    @Override
    @Transactional
    public void leaveGroup(Long activityId, Long groupId, Long userId) {
        ActivityGroupMember member = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found in group"));

        ActivityGroup group = member.getGroup();
        
        if (group.getActivity().getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("ACTIVITY_LOCKED", "Cannot leave group after activity is closed or in progress");
        }

        groupMemberRepository.delete(member);
        group.getMembers().remove(member); // Manually remove for current session check
        
        if (group.getMembers().isEmpty()) {
            groupService.deleteGroupIfEmpty(groupId);
        } else if (member.getRole() == GroupRole.LEADER) {
            // Promote oldest member to leader
            ActivityGroupMember oldestMember = group.getMembers().stream()
                    .min((m1, m2) -> m1.getJoinedAt().compareTo(m2.getJoinedAt()))
                    .orElse(null);
            
            if (oldestMember != null) {
                oldestMember.setRole(GroupRole.LEADER);
                groupMemberRepository.save(oldestMember);
            }
        }

        // If group was locked because it was full, it's now forming again
        if (group.getStatus() == GroupStatus.LOCKED && !group.getMembers().isEmpty()) {
            group.setStatus(GroupStatus.FORMING);
            groupRepository.save(group);
        }
    }
}
