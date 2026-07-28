package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.StudentProfile;
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
    private String phoneNumber;

    public static StudentProfileResponse from(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser() == null ? null : profile.getUser().getId())
                .studentCode(profile.getStudentCode())
                .className(profile.getClassName())
                .major(profile.getMajor())
                .enrollmentYear(profile.getEnrollmentYear())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }
}
