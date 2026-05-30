package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProfileResponse {
    private Long id;
    private UserResponse user;
    private Long userId;
    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;
}
