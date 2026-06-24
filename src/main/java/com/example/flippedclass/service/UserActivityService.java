package com.example.flippedclass.service;

import com.example.flippedclass.dto.UserActivityLogDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface UserActivityService {
    void logActivity(Long userId, String actionType, String description);
    List<UserActivityLogDto> getRecentActivities(Long userId);
    org.springframework.data.domain.Page<UserActivityLogDto> getRecentGlobalActivities(String keyword, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, org.springframework.data.domain.Pageable pageable);
    SseEmitter subscribeToUserActivity(Long userId);
    SseEmitter subscribeToGlobalActivity();
}
