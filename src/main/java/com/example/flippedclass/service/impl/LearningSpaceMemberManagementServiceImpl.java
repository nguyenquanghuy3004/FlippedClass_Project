package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.mentor.MemberDto;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.service.LearningSpaceMemberManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LearningSpaceMemberManagementServiceImpl implements LearningSpaceMemberManagementService {

    private final LearningSpaceMemberRepository memberRepository;

    @Override
    public Page<MemberDto> getSpaceMembers(Long spaceId, Pageable pageable) {
        Page<LearningSpaceMember> members = memberRepository.findByLearningSpaceId(spaceId, pageable);
        return members.map(member -> MemberDto.builder()
                .memberId(member.getId())
                .userId(member.getUser().getId())
                .fullName(member.getUser().getFullName())
                .email(member.getUser().getEmail())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build());
    }

    @Override
    @Transactional
    public void promoteToSupporter(Long spaceId, Long memberId) {
        LearningSpaceMember member = getMember(spaceId, memberId);
        
        if (member.getRole() == MemberRole.OWNER) {
            throw new RuntimeException("Cannot change role of OWNER");
        }
        
        member.setRole(MemberRole.SUPPORTER);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void demoteToMember(Long spaceId, Long memberId) {
        LearningSpaceMember member = getMember(spaceId, memberId);
        
        if (member.getRole() == MemberRole.OWNER) {
            throw new RuntimeException("Cannot change role of OWNER");
        }
        
        member.setRole(MemberRole.MEMBER);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void removeMember(Long spaceId, Long memberId) {
        LearningSpaceMember member = getMember(spaceId, memberId);
        
        if (member.getRole() == MemberRole.OWNER) {
            throw new RuntimeException("Cannot remove OWNER from the space");
        }
        
        memberRepository.delete(member);
    }
    
    private LearningSpaceMember getMember(Long spaceId, Long memberId) {
        return memberRepository.findByIdAndLearningSpaceId(memberId, spaceId)
                .orElseThrow(() -> new RuntimeException("Member not found in this space"));
    }
}
