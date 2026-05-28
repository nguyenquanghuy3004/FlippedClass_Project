package com.example.flippedclass.dto;

import com.example.flippedclass.entity.StudentProfile;

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

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getClassName() {
        return className;
    }

    public String getMajor() {
        return major;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }
}
