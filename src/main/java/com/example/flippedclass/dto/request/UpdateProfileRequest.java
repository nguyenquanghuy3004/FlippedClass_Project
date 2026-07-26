package com.example.flippedclass.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String avatarUrl;
    private String fullName;
    private String phoneNumber;
}