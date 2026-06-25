package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String provider;
    private Set<String> roles;
    private boolean isSupporter;
    private LocalDateTime createdAt;
}
