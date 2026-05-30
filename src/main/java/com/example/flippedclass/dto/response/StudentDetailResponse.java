package com.example.flippedclass.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDetailResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String avatarUrl;

    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;
}