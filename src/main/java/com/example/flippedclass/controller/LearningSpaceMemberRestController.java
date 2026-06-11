package com.example.flippedclass.controller;

import com.example.flippedclass.dto.mentor.MemberDto;
import com.example.flippedclass.service.LearningSpaceMemberManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spaces/{spaceId}/members")
@RequiredArgsConstructor
public class LearningSpaceMemberRestController {

    private final LearningSpaceMemberManagementService memberManagementService;

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @GetMapping
    public ResponseEntity<Page<MemberDto>> getMembers(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(memberManagementService.getSpaceMembers(spaceId, PageRequest.of(page, size)));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER')")
    @PostMapping("/{memberId}/promote")
    public ResponseEntity<Void> promoteToSupporter(@PathVariable Long spaceId, @PathVariable Long memberId) {
        memberManagementService.promoteToSupporter(spaceId, memberId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER')")
    @PostMapping("/{memberId}/demote")
    public ResponseEntity<Void> demoteToMember(@PathVariable Long spaceId, @PathVariable Long memberId) {
        memberManagementService.demoteToMember(spaceId, memberId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER')")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long spaceId, @PathVariable Long memberId) {
        memberManagementService.removeMember(spaceId, memberId);
        return ResponseEntity.ok().build();
    }
}
