package com.example.flippedclass.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminLearningNodeDto {
    private Long id;
    private String title;
    private String description;
    private String nodeType;
    private String status;
    private Long pathId;
    private String pathTitle;
    private String spaceName;
    private Integer itemCount;
    private LocalDateTime createdAt;
}
