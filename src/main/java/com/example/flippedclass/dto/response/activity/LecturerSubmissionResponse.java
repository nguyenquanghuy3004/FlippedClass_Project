package com.example.flippedclass.dto.response.activity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LecturerSubmissionResponse {
    private Long id;
    private String groupName;
    private String githubUrl;
    private String submittedBy;
    private LocalDateTime submittedAt;
    private Double score;
    private String feedback;
}
