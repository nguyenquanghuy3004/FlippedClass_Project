package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.StudentProfile;
import lombok.Getter;

@Getter
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String studentCode;
    private String className;
    private String major;
    private Integer enrollmentYear;

    public static StudentProfileResponse from(StudentProfile profile) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.id = profile.getId();
        response.userId = profile.getUser().getId();
        response.studentCode = profile.getStudentCode();
        response.className = profile.getClassName();
        response.major = profile.getMajor();
        response.enrollmentYear = profile.getEnrollmentYear();
        return response;
    }
}
