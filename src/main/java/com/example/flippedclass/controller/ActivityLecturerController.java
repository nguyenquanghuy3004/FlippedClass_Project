package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.ActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.ActivityStatusUpdateRequest;
import com.example.flippedclass.dto.request.activity.GroupReviewRequest;
import com.example.flippedclass.dto.response.activity.ActivityDetailResponse;
import com.example.flippedclass.dto.response.activity.ActivityResponse;
import com.example.flippedclass.dto.response.activity.GroupDetailResponse;
import com.example.flippedclass.service.ActivityService;
import com.example.flippedclass.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturer")
@RequiredArgsConstructor
public class ActivityLecturerController {

    private final ActivityService activityService;
    private final GroupService groupService;

    // Helper to extract user ID (Assuming it's stored in the principal name for this example)
    private Long getUserId(Authentication authentication) {
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            return 1L; // Fallback mock ID
        }
    }

    @PostMapping("/classrooms/{classroomId}/activities")
    @PreAuthorize("@securityService.isClassroomLecturer(#classroomId, authentication.principal.id)")
    public ResponseEntity<ActivityResponse> createActivity(
            @PathVariable Long classroomId,
            @Valid @RequestBody ActivityCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createActivity(classroomId, request));
    }

    @PatchMapping("/activities/{activityId}/status")
    @PreAuthorize("@securityService.isActivityLecturer(#activityId, authentication.principal.id)")
    public ResponseEntity<ActivityResponse> updateActivityStatus(
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityStatusUpdateRequest request) {
        return ResponseEntity.ok(activityService.updateActivityStatus(activityId, request));
    }

    @GetMapping("/activities/{activityId}")
    @PreAuthorize("@securityService.isActivityLecturer(#activityId, authentication.principal.id)")
    public ResponseEntity<ActivityDetailResponse> getActivityDetails(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(activityService.getActivityDetails(activityId));
    }

    @GetMapping("/activities/{activityId}/groups")
    @PreAuthorize("@securityService.isActivityLecturer(#activityId, authentication.principal.id)")
    public ResponseEntity<List<GroupDetailResponse>> getAllGroups(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(groupService.getAllGroupsByActivity(activityId));
    }

    @PatchMapping("/groups/{groupId}/review")
    @PreAuthorize("@securityService.isGroupLecturer(#groupId, authentication.principal.id)")
    public ResponseEntity<Void> reviewGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupReviewRequest request) {
        groupService.reviewGroup(groupId, request);
        return ResponseEntity.noContent().build();
    }
}
