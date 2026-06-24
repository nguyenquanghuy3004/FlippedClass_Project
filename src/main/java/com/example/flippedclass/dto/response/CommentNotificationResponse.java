package com.example.flippedclass.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationResponse {
    private Long id;
    private Long nodeId;
    private String replierName;
    private String content;
    private String spaceName;
    private Long spaceId;
    private LocalDateTime createdAt;
}