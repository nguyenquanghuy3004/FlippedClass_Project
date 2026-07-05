package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.LecturerActivityUpdateRequest;
import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;
import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;
import com.example.flippedclass.dto.response.activity.LecturerReviewResponse;
import com.example.flippedclass.service.LecturerGroupActivityService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturer/group-activities")
@RequiredArgsConstructor
public class LecturerGroupActivityRestController {

    private final LecturerGroupActivityService lecturerGroupActivityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerActivityResponse createActivity(
            @Valid @RequestBody LecturerActivityCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return lecturerGroupActivityService.createActivity(currentUser.getId(), request);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public List<LecturerActivityResponse> listActivities(
            @RequestParam("spaceId") Long spaceId) {
        return lecturerGroupActivityService.listActivitiesBySpaceId(spaceId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerActivityResponse getActivityDetails(@PathVariable Long id) {
        return lecturerGroupActivityService.getActivityDetails(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerActivityResponse updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody LecturerActivityUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return lecturerGroupActivityService.updateActivity(currentUser.getId(), id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerActivityResponse updateActivityStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return lecturerGroupActivityService.updateActivityStatus(currentUser.getId(), id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public void deleteActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        lecturerGroupActivityService.deleteActivity(currentUser.getId(), id);
    }

    @GetMapping("/{id}/groups")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public List<LecturerGroupResponse> listGroups(
            @PathVariable Long id,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String filter) {
        return lecturerGroupActivityService.listGroupsByActivityId(id, search, filter);
    }

    @GetMapping("/groups/{groupId}/review")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerReviewResponse getGroupReview(@PathVariable Long groupId) {
        return lecturerGroupActivityService.getGroupReview(groupId);
    }

    @PutMapping("/groups/{groupId}/grade")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public LecturerReviewResponse gradeGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody LecturerGradeRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return lecturerGroupActivityService.gradeGroup(currentUser.getId(), groupId, request);
    }

    @PatchMapping("/groups/{groupId}/force-leader")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LECTURER', 'MENTOR', 'ROLE_MENTOR')")
    public void forceAssignLeader(
            @PathVariable Long groupId,
            @RequestParam("newLeaderId") Long newLeaderId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        lecturerGroupActivityService.forceAssignLeader(currentUser.getId(), groupId, newLeaderId);
    }
}
