package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAnalyticsDTO {
    private Long studentId;
    private String studentName;
    private String username;
    private int progressPercentage;
    private long completedNodes;
    private long totalNodes;
    private double quizAvg;
    private String lastActive; // Format: "dd/MM/yyyy HH:mm" or "N/A"
    private boolean supporterCandidate;

}
