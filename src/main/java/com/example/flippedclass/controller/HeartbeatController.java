package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.HeartbeatRequest;
import com.example.flippedclass.security.CustomUserDetails;
import com.example.flippedclass.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class HeartbeatController {

    private final UserService userService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @Valid @RequestBody HeartbeatRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails != null && userDetails.getId() != null) {
            userService.updateActiveTime(userDetails.getId(), request.getActiveSeconds());
        }
        return ResponseEntity.ok().build();
    }
}
