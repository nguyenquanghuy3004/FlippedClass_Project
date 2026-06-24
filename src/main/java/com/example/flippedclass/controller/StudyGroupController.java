package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.ReviewRequest;
import com.example.flippedclass.dto.request.activity.StudyGroupCreateRequest;
import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.dto.request.activity.TransferLeaderRequest;
import com.example.flippedclass.dto.response.activity.AvailableGroupResponse;
import com.example.flippedclass.dto.response.activity.StudyGroupDetailResponse;
import com.example.flippedclass.service.StudyGroupMemberService;
import com.example.flippedclass.service.StudyGroupService;
import com.example.flippedclass.service.SubmissionService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService groupService;
    private final StudyGroupMemberService memberService;
    private final SubmissionService submissionService;

    // Lấy nhóm còn trống
    @GetMapping("/activities/{activityId}/available-groups")
    @PreAuthorize("hasAuthority('STUDENT')")
    public List<AvailableGroupResponse> getAvailableGroups(@PathVariable Long activityId) {
        return groupService.getAvailableGroups(activityId);
    }

    // Lấy nhóm của tôi
    @GetMapping("/activities/{activityId}/my-group")
    @PreAuthorize("hasAuthority('STUDENT')")
    public StudyGroupDetailResponse getMyGroup(
            @PathVariable Long activityId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return groupService.getMyGroup(activityId, currentUser.getId());
    }

    // Tạo nhóm mới
    @PostMapping("/activities/{activityId}/groups")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('STUDENT')")
    public StudyGroupDetailResponse createGroup(
            @PathVariable Long activityId,
            @RequestBody StudyGroupCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return groupService.createGroup(activityId, currentUser.getId(), request);
    }

    // Join nhóm
    @PostMapping("/activities/{activityId}/groups/{groupId}/join")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void joinGroup(
            @PathVariable Long activityId, 
            @PathVariable Long groupId,
            @RequestParam String inviteCode,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        memberService.joinGroup(activityId, groupId, currentUser.getId(), inviteCode);
    }

    // Rời nhóm
    @DeleteMapping("/groups/{groupId}/leave")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        memberService.leaveGroup(groupId, currentUser.getId());
    }

    // Nộp bài
    @PutMapping("/groups/{groupId}/submission")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void submitWork(
            @PathVariable Long groupId, 
            @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        submissionService.submitWork(groupId, currentUser.getId(), request);
    }

    // Chấm điểm
    @PatchMapping("/groups/{groupId}/review")
    @PreAuthorize("hasAuthority('LECTURER')")
    public void reviewGroup(@PathVariable Long groupId, @RequestBody ReviewRequest request) {
        groupService.reviewGroup(groupId, request);
    }
    
    // Đổi leader
    @PatchMapping("/groups/{groupId}/transfer-leader")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void transferLeader(
            @PathVariable Long groupId, 
            @RequestBody TransferLeaderRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        memberService.transferLeader(groupId, currentUser.getId(), request.getNewLeaderId());
    }
}
