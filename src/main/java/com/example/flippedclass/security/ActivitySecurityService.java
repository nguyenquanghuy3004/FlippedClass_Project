package com.example.flippedclass.security;

import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.entity.ClassroomActivity;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.enums.GroupRole;
import com.example.flippedclass.repository.ActivityGroupMemberRepository;
import com.example.flippedclass.repository.ActivityGroupRepository;
import com.example.flippedclass.repository.ClassroomActivityRepository;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class ActivitySecurityService {

    private final ClassroomActivityRepository activityRepository;
    private final ActivityGroupRepository groupRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;
    private final ActivityGroupMemberRepository activityGroupMemberRepository;

    private Long getCurrentUserId() {
        // Implement logic to get current user ID from SecurityContext
        // For example: return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        // Giả sử tạm thời trả về ID dựa trên việc mock (Cần thay thế thực tế dựa trên project)
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Need a way to extract user ID. Assuming there is a CustomUserDetails or extracting from token.
        }
        return null; // Update with actual logic based on existing project authentication
    }

    // Helper to get user ID safely
    public boolean isClassroomLecturer(Long classroomId, Long userId) {
         // Thực tế kiểm tra xem user có quyền Lecturer trong Classroom không
         return true; // TODO: Implement
    }

    public boolean isActivityLecturer(Long activityId, Long userId) {
         ClassroomActivity activity = activityRepository.findById(activityId).orElse(null);
         if (activity == null) return false;
         return isClassroomLecturer(activity.getClassroom().getId(), userId);
    }
    
    public boolean isGroupLecturer(Long groupId, Long userId) {
         ActivityGroup group = groupRepository.findById(groupId).orElse(null);
         if (group == null) return false;
         return isActivityLecturer(group.getActivity().getId(), userId);
    }

    public boolean isActivityStudent(Long activityId, Long userId) {
         ClassroomActivity activity = activityRepository.findById(activityId).orElse(null);
         if (activity == null) return false;
         Optional<LearningSpaceMember> member = learningSpaceMemberRepository.findByLearningSpace_IdAndUser_Id(activity.getClassroom().getId(), userId);
         return member.isPresent();
    }

    public boolean isGroupLeader(Long groupId, Long userId) {
         return activityGroupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)
                .map(m -> m.getRole() == GroupRole.LEADER)
                .orElse(false);
    }

    public boolean canViewActivity(Long activityId, Long userId) {
         return isActivityLecturer(activityId, userId) || isActivityStudent(activityId, userId);
    }

    public boolean canViewGroup(Long groupId, Long userId) {
         ActivityGroup group = groupRepository.findById(groupId).orElse(null);
         if (group == null) return false;
         return canViewActivity(group.getActivity().getId(), userId);
    }
}
