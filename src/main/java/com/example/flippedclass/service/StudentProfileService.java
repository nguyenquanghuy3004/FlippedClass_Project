package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.UpdateProfileRequest;
import com.example.flippedclass.dto.response.StudentDetailResponse;

public interface StudentProfileService {
    StudentDetailResponse getStudentDetail(Long studentId);
    StudentDetailResponse updateProfile(Long userId, UpdateProfileRequest request);
}