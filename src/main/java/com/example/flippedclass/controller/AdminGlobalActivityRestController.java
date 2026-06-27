package com.example.flippedclass.controller;

import com.example.flippedclass.dto.UserActivityLogDto;
import com.example.flippedclass.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/admin/users/global")
@RequiredArgsConstructor
public class AdminGlobalActivityRestController {

    private final UserActivityService userActivityService;

    @GetMapping("/activities")
    public ResponseEntity<org.springframework.data.domain.Page<UserActivityLogDto>> getGlobalActivities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate filterDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        java.time.LocalDateTime startOfDay = filterDate != null ? filterDate.atStartOfDay() : null;
        java.time.LocalDateTime endOfDay = filterDate != null ? filterDate.atTime(23, 59, 59) : null;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(userActivityService.getRecentGlobalActivities(keyword, startOfDay, endOfDay, pageable));
    }

    @GetMapping("/activity-stream")
    public SseEmitter streamGlobalActivities() {
        return userActivityService.subscribeToGlobalActivity();
    }
}
