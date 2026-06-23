package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;
import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;
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
    @PreAuthorize("hasAuthority('LECTURER')")
    public LecturerActivityResponse createActivity(
            @Valid @RequestBody LecturerActivityCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return lecturerGroupActivityService.createActivity(currentUser.getId(), request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LECTURER')")
    public List<LecturerActivityResponse> listActivities(
            @RequestParam("spaceId") Long spaceId) {
        return lecturerGroupActivityService.listActivitiesBySpaceId(spaceId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LECTURER')")
    public LecturerActivityResponse getActivityDetails(@PathVariable Long id) {
        return lecturerGroupActivityService.getActivityDetails(id);
    }

    @GetMapping("/{id}/groups")
    @PreAuthorize("hasAuthority('LECTURER')")
    public List<LecturerGroupResponse> listGroups(
            @PathVariable Long id,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String filter) {
        return lecturerGroupActivityService.listGroupsByActivityId(id, search, filter);
    }
}
