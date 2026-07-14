package com.example.flippedclass.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreateDto {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private List<String> roles;
}
