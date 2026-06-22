package com.example.flippedclass.service;

import com.example.flippedclass.dto.UserActivityLogDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface UserActivityService {
    void logActivity(Long userId, String actionType, String description);
    List<UserActivityLogDto> getRecentActivities(Long userId);
    SseEmitter subscribeToUserActivity(Long userId);
}
