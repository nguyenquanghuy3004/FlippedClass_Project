package com.example.flippedclass.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassWord;
    private String confirmPassword;
}
