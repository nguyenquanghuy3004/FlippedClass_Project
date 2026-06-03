package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private Long learningNodeId;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Boolean active;
    private LocalDateTime createdAt;
    
    // Additional Metadata
    private Integer passScore;
    private String difficulty;
    private String thumbnailUrl;
    private String courseName;
    private Long learningSpaceId;
    private String learningSpaceName;
    
    // Statistical fields
    private Integer questionCount;
    private Long totalAttempts;
    private Double averageScore;
    private Double passRate;
}
