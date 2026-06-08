package com.example.flippedclass.dto;

import com.example.flippedclass.enums.LearningSpaceStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LearningSpaceDto {
    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private String inviteCode;
    private LearningSpaceStatus  status;
    private LocalDateTime createdAt;
}
