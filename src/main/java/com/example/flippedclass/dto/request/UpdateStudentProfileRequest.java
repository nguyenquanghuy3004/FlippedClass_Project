package com.example.flippedclass.dto.request;

import lombok.Data;

@Data
public class UpdateStudentProfileRequest {
    private String fullName;
    private String avatarUrl;
    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;
}
