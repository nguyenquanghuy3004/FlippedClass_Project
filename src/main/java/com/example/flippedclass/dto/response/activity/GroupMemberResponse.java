package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private Long userId;
    private String fullName;
    private String username;
    private GroupRole role;
    private LocalDateTime joinedAt;
}
