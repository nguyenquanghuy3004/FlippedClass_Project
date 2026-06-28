package com.example.flippedclass.controller;

import com.example.flippedclass.entity.Notification;
import com.example.flippedclass.repository.NotificationRepository;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    // Hàm dùng chung để các class khác gọi khi muốn tạo thông báo
    public void pushNotification(Long lecturerId, String message, String link) {
        // 1. Lưu DB
        Notification n = Notification.builder()
                .recipientId(lecturerId)
                .message(message)
                .link(link)
                .isRead(false)
                .build();
        repo.save(n);

        // 2. Đẩy qua WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + lecturerId, n);
    }

    // API lấy danh sách cho Frontend
    @GetMapping
    public List<Notification> getMyNotifs(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return repo.findByRecipientIdOrderByCreatedAtDesc(user.getId());
    }

    // API đánh dấu đã đọc
    @GetMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
        });
    }
}
