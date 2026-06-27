package com.example.flippedclass.service.impl;

import com.example.flippedclass.entity.StudyGroup;
import com.example.flippedclass.entity.StudyGroupMember;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.StudyGroupMemberRepository;
import com.example.flippedclass.repository.StudyGroupRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.StudyGroupMemberService;
import com.example.flippedclass.validation.GroupActivityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupMemberServiceImpl implements StudyGroupMemberService {

    private final StudyGroupRepository groupRepository;
    private final StudyGroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GroupActivityValidator validator;

    @Override
    @Transactional
    public void joinGroup(Long activityId, Long groupId, Long currentUserId, String inviteCode) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!group.getActivity().getId().equals(activityId)) {
            throw new BusinessException("ACTIVITY_MISMATCH: Group does not belong to this activity");
        }

        validator.validateActivityOpen(group.getActivity());
        validator.validateUserNotInAnyGroup(currentUserId, activityId);
        validator.validateGroupCapacity(group, group.getActivity());

        if (inviteCode == null || !inviteCode.equals(group.getInviteCode())) {
            throw new BusinessException("INVALID_INVITE_CODE: Mã mời không chính xác");
        }

        StudyGroupMember member = StudyGroupMember.builder()
                .group(group)
                .student(user)
                .role(GroupRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long currentUserId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        StudyGroupMember member = memberRepository.findByGroupIdAndStudentId(groupId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Member not found in this group"));

        memberRepository.delete(member);
        group.getMembers().remove(member);

        if (group.getMembers().isEmpty()) {
            groupRepository.delete(group);
        } else if (group.getLeader().getId().equals(currentUserId)) {
            // Auto promote first available member
            StudyGroupMember nextLeader = group.getMembers().iterator().next();
            nextLeader.setRole(GroupRole.LEADER);
            group.setLeader(nextLeader.getStudent());
            groupRepository.save(group);
            memberRepository.save(nextLeader);
        }
    }

    @Override
    @Transactional
    public void transferLeader(Long groupId, Long currentUserId, Long newLeaderId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        if (!group.getLeader().getId().equals(currentUserId)) {
            throw new BusinessException("ONLY_LEADER: Only current leader can transfer role");
        }

        StudyGroupMember currentLeaderMember = memberRepository.findByGroupIdAndStudentId(groupId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Leader member not found"));

        StudyGroupMember newLeaderMember = memberRepository.findByGroupIdAndStudentId(groupId, newLeaderId)
                .orElseThrow(() -> new NotFoundException("New leader not found in this group"));

        currentLeaderMember.setRole(GroupRole.MEMBER);
        newLeaderMember.setRole(GroupRole.LEADER);
        group.setLeader(newLeaderMember.getStudent());

        memberRepository.saveAll(List.of(currentLeaderMember, newLeaderMember));
        groupRepository.save(group);
    }
}
