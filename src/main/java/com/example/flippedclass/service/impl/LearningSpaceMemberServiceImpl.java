package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.mentor.MemberDto;
import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.service.LearningSpaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningSpaceMemberServiceImpl implements LearningSpaceMemberService {

    private final LearningSpaceMemberRepository memberRepository;
    private final com.example.flippedclass.repository.StudyGroupMemberRepository studyGroupMemberRepository;
    private final com.example.flippedclass.service.StudyGroupMemberService studyGroupMemberService;
    private final com.example.flippedclass.service.PeerMentoringService peerMentoringService;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDto> getSpaceMembers(Long spaceId, Pageable pageable) {
        Page<LearningSpaceMember> members = memberRepository.findByLearningSpaceId(spaceId, pageable);
        return members.map(member -> MemberDto.builder()
                .memberId(member.getId())
                .userId(member.getUser().getId())
                .studentCode(member.getUser().getStudentProfile() != null && member.getUser().getStudentProfile().getStudentCode() != null 
                             ? member.getUser().getStudentProfile().getStudentCode() 
                             : member.getUser().getUsername())
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
        
        // Remove from all study groups within this space
        java.util.List<com.example.flippedclass.entity.StudyGroupMember> groupMembers = 
                studyGroupMemberRepository.findByStudentIdAndLearningSpaceId(member.getUser().getId(), spaceId);
        
        for (com.example.flippedclass.entity.StudyGroupMember gm : groupMembers) {
            studyGroupMemberService.leaveGroup(gm.getGroup().getId(), member.getUser().getId());
        }

        memberRepository.delete(member);
    }
    
    private LearningSpaceMember getMember(Long spaceId, Long memberId) {
        return memberRepository.findByIdAndLearningSpaceId(memberId, spaceId)
                .orElseThrow(() -> new RuntimeException("Member not found in this space"));
    }
}
