package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(String username);
    long getUnreadCount(String username);
    void markAsRead(Long id, String username);
    void markAllAsRead(String username);
    void pushNotification(Long recipientId, String message, String targetUrl);
}
