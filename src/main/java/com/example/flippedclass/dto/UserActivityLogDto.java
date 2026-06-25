package com.example.flippedclass.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserActivityLogDto {
    private Long id;
    private String actionType;
    private String description;
    private LocalDateTime createdAt;
}
