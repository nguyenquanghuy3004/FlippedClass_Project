package com.example.flippedclass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedSolutionDto {
    private Long id;
    private Long learningNodeId;
    private Long studentId;
    private String studentName;
    private String title;
    private String codeContent;
    private LocalDateTime createdAt;
    private int upvotes;
}
