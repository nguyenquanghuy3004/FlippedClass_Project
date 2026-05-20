package com.example.flippedclass.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}