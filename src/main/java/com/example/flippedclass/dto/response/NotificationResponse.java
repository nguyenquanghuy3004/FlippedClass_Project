package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
