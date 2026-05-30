package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.UpdateProfileRequest;
import com.example.flippedclass.dto.response.StudentDetailResponse;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.StudentProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileServiceImpl(UserRepository userRepository, StudentProfileRepository studentProfileRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Override
    public StudentDetailResponse getStudentDetail(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hồ sơ sinh viên!"));

        return mapToResponse(user, profile);
    }

    @Override
    @Transactional
    public StudentDetailResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hồ sơ sinh viên!"));

        // Chỉ cập nhật avatarUrl ở bảng User theo đúng chốt hạ của nhóm
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        return mapToResponse(user, profile);
    }

    private StudentDetailResponse mapToResponse(User user, StudentProfile profile) {
        StudentDetailResponse res = new StudentDetailResponse();

        res.setUserId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setAvatarUrl(user.getAvatarUrl());

        res.setStudentCode(profile.getStudentCode());
        res.setClassName(profile.getClassName());
        res.setMajor(profile.getMajor());
        res.setEnrollmentYear(profile.getEnrollmentYear());

        return res;
    }
}