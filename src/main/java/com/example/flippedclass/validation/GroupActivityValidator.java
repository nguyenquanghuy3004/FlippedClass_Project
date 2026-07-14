package com.example.flippedclass.validation;

import com.example.flippedclass.entity.GroupActivity;
import com.example.flippedclass.entity.StudyGroup;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GroupActivityValidator {

    private final StudyGroupMemberRepository memberRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;

    public void validateClassroomMember(Long studentId, Long learningSpaceId) {
        boolean isMember = learningSpaceMemberRepository.existsByLearningSpaceIdAndUserId(learningSpaceId, studentId);
        if (!isMember) {
            throw new BusinessException("NOT_CLASSROOM_MEMBER: Student is not a member of this learning space");
        }
    }

    public void validateUserNotInAnyGroup(Long studentId, Long activityId) {
        boolean exists = memberRepository.existsByActivityIdAndStudentId(activityId, studentId);
        if (exists) {
            throw new BusinessException("ALREADY_IN_GROUP: Student already in a group for this activity");
        }
    }

    public void validateGroupCapacity(StudyGroup group, GroupActivity activity) {
        if (group.getMembers().size() >= activity.getMaxMembers()) {
            throw new BusinessException("GROUP_FULL: Group has reached maximum capacity");
        }
    }

    public void validateActivityOpen(GroupActivity activity) {
        if (activity.getStatus() != ActivityStatus.OPEN && activity.getStatus() != ActivityStatus.IN_PROGRESS) {
            throw new BusinessException("ACTIVITY_NOT_OPEN: Activity is not open");
        }
    }

    public void validateDeadline(GroupActivity activity) {
        if (activity.getDeadline() != null && LocalDateTime.now().isAfter(activity.getDeadline())) {
            throw new BusinessException("DEADLINE_PASSED: Activity deadline has passed");
        }
    }

    public void validateLeaderRole(Long studentId, StudyGroup group) {
        if (!group.getLeader().getId().equals(studentId)) {
            throw new BusinessException("ONLY_LEADER_CAN_SUBMIT: Only group leader can perform this action");
        }
    }
}
