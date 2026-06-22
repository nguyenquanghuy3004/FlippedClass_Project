package com.example.flippedclass.controller;

import com.example.flippedclass.dto.UserActivityLogDto;
import com.example.flippedclass.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users/{userId}")
@RequiredArgsConstructor
public class AdminActivityRestController {

    private final UserActivityService userActivityService;

    @GetMapping("/activities")
    public ResponseEntity<List<UserActivityLogDto>> getRecentActivities(@PathVariable Long userId) {
        return ResponseEntity.ok(userActivityService.getRecentActivities(userId));
    }

    @GetMapping("/activity-stream")
    public SseEmitter streamActivities(@PathVariable Long userId) {
        return userActivityService.subscribeToUserActivity(userId);
    }
}
