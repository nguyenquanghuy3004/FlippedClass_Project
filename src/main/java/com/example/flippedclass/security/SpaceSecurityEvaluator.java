package com.example.flippedclass.security;

import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component("spaceSecurity")
public class SpaceSecurityEvaluator {

    @Autowired
    private LearningSpaceMemberRepository memberRepository;

    @Autowired
    private com.example.flippedclass.repository.LearningSpaceRepository learningSpaceRepository;

    public boolean hasRoleInSpace(Long spaceId, String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // ĐẶC QUYỀN ADMIN: Nếu người dùng là Admin hệ thống thì tự động cho phép truy cập luôn
        for (org.springframework.security.core.GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN")) {
                return true;
            }
        }
        
        String username = auth.getName();

        Optional<LearningSpaceMember> memberOpt = memberRepository
                .findByLearningSpaceIdAndUserUsername(spaceId, username);

        if (memberOpt.isPresent()) {
            LearningSpaceMember member = memberOpt.get();
            // Kiểm tra xem vai trò của thành viên có thuộc danh sách các vai trò được phép không
            return Arrays.asList(roles).contains(member.getRole().name());
        }

        // Nếu không có member record, kiểm tra xem có phải là PUBLIC không
        com.example.flippedclass.entity.LearningSpace space = learningSpaceRepository.findById(spaceId).orElse(null);
        if (space != null && space.getVisibility() == com.example.flippedclass.enums.VisibilityType.PUBLIC 
                && space.getStatus() == com.example.flippedclass.enums.LearningSpaceStatus.ACTIVE) {
            return Arrays.asList(roles).contains("MEMBER");
        }

        return false;
    }
}
