package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudyGroupDetailResponse {
    private Long id;
    private String groupName;
    private String inviteCode;
    private Long leaderId;
    
    private List<MemberInfo> members;
    private SubmissionInfo submission;

    @Data
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String fullName;
        private String role;
        private LocalDateTime joinedAt;
    }

    @Data
    @Builder
    public static class SubmissionInfo {
        private String githubRepoUrl;
        private LocalDateTime submittedAt;
        private Double score;
        private String feedback;
        private SubmissionStatus status;
    }
}
