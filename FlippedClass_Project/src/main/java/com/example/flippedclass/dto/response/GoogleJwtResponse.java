package com.example.flippedclass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleJwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private List<String> roles;
    private boolean profileComplete;

    public GoogleJwtResponse(String token, Long id, String username, String email, String fullName, String avatarUrl, List<String> roles, boolean profileComplete) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.profileComplete = profileComplete;
    }
}
