package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.UpdateStudentProfileRequest;
import com.example.flippedclass.dto.response.DashboardLearningSpaceResponse;
import com.example.flippedclass.dto.response.DashboardUserResponse;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.dto.response.StudentProfileResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.CourseDocumentRepository;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.QuizQuestionRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.StudentDashboardService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;
    private final com.example.flippedclass.repository.QuizRepository quizRepository;
    private final com.example.flippedclass.repository.QuizAttemptRepository quizAttemptRepository;

    private final QuizQuestionRepository quizQuestionRepository;
    private final CourseDocumentRepository courseDocumentRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboard(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student user not found"));

        StudentProfileResponse profile = studentProfileRepository.findByUser_Id(studentId)
                .map(this::toStudentProfileResponse)
                .orElse(null);

        List<LearningSpaceMember> memberships = learningSpaceMemberRepository.findByUser_IdOrderByJoinedAtDesc(studentId);
        List<Long> learningSpaceIds = memberships.stream()
                .map(member -> member.getLearningSpace().getId())
                .distinct()
                .toList();

        List<DashboardLearningSpaceResponse> learningSpaces =
                memberships.stream()
                        .map(member -> {
                            Long spaceId = member.getLearningSpace().getId();
                            List<QuizResponse> quizzes = quizRepository.findByLearningNode_LearningPath_LearningSpace_IdAndActiveTrue(spaceId).stream()
                                    .map(q -> QuizResponse.builder()
                                            .id(q.getId())
                                            .title(q.getTitle())
                                            .durationMinutes(q.getDurationMinutes())
                                            .isCompleted(quizAttemptRepository.existsByQuizIdAndStudent_Id(q.getId(), studentId))
                                            .build())
                                    .collect(Collectors.toList());
                            return DashboardLearningSpaceResponse.from(member, quizzes);
                        })

                        .toList();

        List<CourseDocumentResponse> recentDocuments = learningSpaceIds.isEmpty()
                ? List.of()
                : courseDocumentRepository
                        .findTop8ByLearningPath_LearningSpace_IdInOrderByCreatedAtDesc(learningSpaceIds)
                        .stream()
                        .map(CourseDocumentResponse::from)
                        .toList();

        return new StudentDashboardResponse(
                DashboardUserResponse.from(user),
                profile,
                learningSpaces,
                recentDocuments
        );
    }

    @Override
    @Transactional
    public StudentDashboardResponse updateProfile(Long studentId, UpdateStudentProfileRequest request) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student user not found"));

        if (request.getFullName() != null) {
            user.setFullName(trimToNull(request.getFullName()));
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(trimToNull(request.getAvatarUrl()));
        }

        StudentProfile profile = studentProfileRepository.findByUser_Id(studentId)
                .orElseGet(() -> {
                    StudentProfile created = new StudentProfile();
                    created.setUser(user);
                    return created;
                });

        String requestedStudentCode = trimToNull(request.getStudentCode());
        if (requestedStudentCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student code is required");
        }

        studentProfileRepository.findByStudentCode(requestedStudentCode)
                .filter(existing -> profile.getId() == null || !existing.getId().equals(profile.getId()))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student code is already taken");
                });

        profile.setStudentCode(requestedStudentCode);
        profile.setClassName(trimToNull(request.getClassName()));
        profile.setMajor(trimToNull(request.getMajor()));
        profile.setEnrollmentYear(request.getEnrollmentYear());

        userRepository.save(user);
        studentProfileRepository.save(profile);

        return getDashboard(studentId);
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
