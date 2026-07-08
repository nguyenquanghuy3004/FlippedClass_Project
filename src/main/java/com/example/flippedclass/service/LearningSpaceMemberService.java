package com.example.flippedclass.service;

import com.example.flippedclass.dto.mentor.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LearningSpaceMemberService {
    Page<MemberDto> getSpaceMembers(Long spaceId, Pageable pageable);
    void promoteToSupporter(Long spaceId, Long memberId);
    void demoteToMember(Long spaceId, Long memberId);
    void removeMember(Long spaceId, Long memberId);
}
