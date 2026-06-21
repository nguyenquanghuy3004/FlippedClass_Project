package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long groupId;
    private String githubUrl;
    private String note;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private Long submittedById;
    private String submittedByFullName;
}
