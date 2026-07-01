package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
