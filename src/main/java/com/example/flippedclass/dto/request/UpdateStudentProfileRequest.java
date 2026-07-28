package com.example.flippedclass.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileRequest {
    private String fullName;
    private String avatarUrl;
    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;
    private String phoneNumber;
}
