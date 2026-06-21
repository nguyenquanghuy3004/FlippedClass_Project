package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.GroupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
    private Long id;
    private String groupName;
    private Double finalGrade;
    private String gradeComment;
    private LocalDateTime gradedAt;
    private GroupStatus status;
    
    private List<GroupMemberResponse> members;
    private SubmissionResponse submission;
}
