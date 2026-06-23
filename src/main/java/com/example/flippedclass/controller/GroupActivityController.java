package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.GroupActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.GroupActivityResponse;
import com.example.flippedclass.service.GroupActivityService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class GroupActivityController {

    private final GroupActivityService activityService;

    @PostMapping("/nodes/{nodeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LECTURER')")
    public GroupActivityResponse createActivity(
            @PathVariable Long nodeId,
            @RequestBody GroupActivityCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return activityService.createActivity(nodeId, currentUser.getId(), request);
    }

    @GetMapping("/{id}")
    public GroupActivityResponse getActivityDetails(@PathVariable Long id) {
        return activityService.getActivityDetails(id);
    }

    @GetMapping("/nodes/{nodeId}/details")
    public GroupActivityResponse getActivityByNodeId(@PathVariable Long nodeId) {
        return activityService.getActivityByNodeId(nodeId);
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('LECTURER')")
    public void publishActivity(@PathVariable Long id) {
        activityService.publishActivity(id);
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAuthority('LECTURER')")
    public void closeActivity(@PathVariable Long id) {
        activityService.closeActivity(id);
    }
}
