package com.example.flippedclass.dto.response.activity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LecturerGroupResponse {
    private Long groupId;
    private String groupName;
    private String leaderName;
    private int currentMembers;
    private int maxMembers;
    private String githubUrl;
    private String status;
}
