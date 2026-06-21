package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.GroupCreateRequest;
import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.dto.response.activity.ActivityDetailResponse;
import com.example.flippedclass.dto.response.activity.GroupDetailResponse;
import com.example.flippedclass.dto.response.activity.GroupMemberResponse;
import com.example.flippedclass.dto.response.activity.GroupResponse;
import com.example.flippedclass.dto.response.activity.SubmissionResponse;
import com.example.flippedclass.service.ActivityService;
import com.example.flippedclass.service.GroupMemberService;
import com.example.flippedclass.service.GroupService;
import com.example.flippedclass.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class ActivityStudentController {

    private final ActivityService activityService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final SubmissionService submissionService;

    // In a real application, you might use a CustomUserDetails object directly
    private Long getUserId(Authentication authentication) {
        try {
            // Using a mock way to extract user ID. Assuming authentication principal holds the user object
            // which has a getId() method, or the name is the ID.
            // Replace with actual implementation like: ((CustomUserDetails) authentication.getPrincipal()).getId()
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            return 2L; // Fallback mock ID for student
        }
    }

    @GetMapping("/activities/{activityId}")
    @PreAuthorize("@securityService.isActivityStudent(#activityId, authentication.principal.id)")
    public ResponseEntity<ActivityDetailResponse> getActivityDetails(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(activityService.getActivityDetails(activityId));
    }

    @GetMapping("/activities/{activityId}/available-groups")
    @PreAuthorize("@securityService.isActivityStudent(#activityId, authentication.principal.id)")
    public ResponseEntity<List<GroupResponse>> getAvailableGroups(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(groupService.getAvailableGroups(activityId));
    }

    @PostMapping("/activities/{activityId}/groups")
    @PreAuthorize("@securityService.isActivityStudent(#activityId, authentication.principal.id)")
    public ResponseEntity<GroupDetailResponse> createGroup(
            @PathVariable Long activityId,
            @Valid @RequestBody GroupCreateRequest request,
            Authentication authentication) {
        // Need to pass the user ID from the authentication context
        // Here we assume getUserId() retrieves it properly
        // In the @PreAuthorize we used authentication.principal.id as a Spring EL example
        Long userId = getUserId(authentication); 
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(activityId, userId, request));
    }

    @PostMapping("/activities/{activityId}/groups/{groupId}/join")
    @PreAuthorize("@securityService.isActivityStudent(#activityId, authentication.principal.id)")
    public ResponseEntity<GroupMemberResponse> joinGroup(
            @PathVariable Long activityId,
            @PathVariable Long groupId,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(groupMemberService.joinGroup(activityId, groupId, userId));
    }

    @PostMapping("/activities/{activityId}/groups/{groupId}/leave")
    @PreAuthorize("@securityService.isActivityStudent(#activityId, authentication.principal.id)")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long activityId,
            @PathVariable Long groupId,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        groupMemberService.leaveGroup(activityId, groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/groups/{groupId}")
    @PreAuthorize("@securityService.canViewGroup(#groupId, authentication.principal.id)")
    public ResponseEntity<GroupDetailResponse> getGroupDetails(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetail(groupId));
    }

    @PutMapping("/groups/{groupId}/submission")
    @PreAuthorize("@securityService.canViewGroup(#groupId, authentication.principal.id)")
    public ResponseEntity<SubmissionResponse> submitWork(
            @PathVariable Long groupId,
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(submissionService.submitWork(groupId, userId, request));
    }
}
