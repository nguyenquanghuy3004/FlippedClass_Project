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

    public boolean hasRoleInSpace(Long spaceId, String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // ĐẶC QUYỀN ADMIN & MENTOR: Nếu người dùng là Admin hoặc Giảng viên hệ thống thì tự động cho phép truy cập luôn
        for (org.springframework.security.core.GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN") || authority.getAuthority().equals("MENTOR")) {
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

        return false;
    }

    public boolean isMemberInSpace(Long spaceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        for (org.springframework.security.core.GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN") || authority.getAuthority().equals("MENTOR")) {
                return true;
            }
        }

        String username = auth.getName();
        return memberRepository.findByLearningSpaceIdAndUserUsername(spaceId, username).isPresent();
    }
}
