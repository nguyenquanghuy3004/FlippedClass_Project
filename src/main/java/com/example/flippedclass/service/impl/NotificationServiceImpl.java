package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.NotificationResponse;
import com.example.flippedclass.entity.Notification;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.NotificationRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
        
        return notifications.stream().map(n -> NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .targetUrl(n.getTargetUrl())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
                
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        notificationRepository.markAllAsReadForUser(user.getId());
    }

    @Override
    @Transactional
    public void pushNotification(Long recipientId, String message, String targetUrl) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        com.example.flippedclass.enums.NotificationType type = com.example.flippedclass.enums.NotificationType.COMMENT_REPLY;
        if (message != null && message.toLowerCase().contains("tóm tắt")) {
            type = com.example.flippedclass.enums.NotificationType.REVIEW_MENTOR;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .message(message)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }
}
