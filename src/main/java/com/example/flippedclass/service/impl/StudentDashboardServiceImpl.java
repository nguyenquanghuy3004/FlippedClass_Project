package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.DashboardLearningSpaceResponse;
import com.example.flippedclass.dto.response.DashboardUserResponse;
import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.dto.response.StudentProfileResponse;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.StudentDashboardService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;

    public StudentDashboardServiceImpl(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            LearningSpaceMemberRepository learningSpaceMemberRepository
    ) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.learningSpaceMemberRepository = learningSpaceMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboard(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student user not found"));

        StudentProfileResponse profile = studentProfileRepository.findByUser_Id(studentId)
                .map(this::toStudentProfileResponse)
                .orElse(null);

        List<DashboardLearningSpaceResponse> learningSpaces =
                learningSpaceMemberRepository.findByUser_IdOrderByJoinedAtDesc(studentId).stream()
                        .map(DashboardLearningSpaceResponse::from)
                        .toList();

        return new StudentDashboardResponse(
                DashboardUserResponse.from(user),
                profile,
                learningSpaces
        );
    }

    private com.example.flippedclass.dto.response.StudentProfileResponse toStudentProfileResponse(com.example.flippedclass.entity.StudentProfile profile) {
        if (profile == null) return null;
        return com.example.flippedclass.dto.response.StudentProfileResponse.builder()
            .id(profile.getId())
            .userId(profile.getUser() != null ? profile.getUser().getId() : null)
            .studentCode(profile.getStudentCode())
            .major(profile.getMajor())
            .className(profile.getClassName())
            .enrollmentYear(profile.getEnrollmentYear())
            .build();
    }
}
