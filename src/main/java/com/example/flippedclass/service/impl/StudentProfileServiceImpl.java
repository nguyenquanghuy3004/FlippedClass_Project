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
                .orElse(null); // Không throw lỗi để Frontend vẫn lấy được thông tin User

        return mapToResponse(user, profile);
    }

    @Override
    @Transactional
    public StudentDetailResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElse(new StudentProfile()); // Tạo mới nếu chưa có
        
        if (profile.getUser() == null) {
            profile.setUser(user);
            profile.setStudentCode("TEMP_" + user.getUsername()); // Fix lỗi null studentCode
        }

        // Cập nhật fullName
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Chỉ cập nhật avatarUrl ở bảng User theo đúng chốt hạ của nhóm
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        userRepository.save(user);

        // Cập nhật phone và bio vào bảng StudentProfile
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        studentProfileRepository.save(profile);

        return mapToResponse(user, profile);
    }

    private StudentDetailResponse mapToResponse(User user, StudentProfile profile) {
        StudentDetailResponse res = new StudentDetailResponse();

        res.setUserId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setAvatarUrl(user.getAvatarUrl());

        if (profile != null) {
            res.setStudentCode(profile.getStudentCode());
            res.setClassName(profile.getClassName());
            res.setMajor(profile.getMajor());
            res.setEnrollmentYear(profile.getEnrollmentYear());
            res.setPhoneNumber(profile.getPhoneNumber());
            res.setBio(profile.getBio());
        }

        return res;
    }
}