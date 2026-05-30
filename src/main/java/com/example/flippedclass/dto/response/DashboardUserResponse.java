package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.User;
import java.time.LocalDateTime;

public class DashboardUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DashboardUserResponse from(User user) {
        DashboardUserResponse response = new DashboardUserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.email = user.getEmail();
        response.fullName = user.getFullName();
        response.avatarUrl = user.getAvatarUrl();
        response.provider = user.getProvider();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getProvider() {
        return provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
