package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.UpdateStudentProfileRequest;
import com.example.flippedclass.dto.response.DashboardLearningSpaceResponse;
import com.example.flippedclass.dto.response.DashboardUserResponse;
import com.example.flippedclass.dto.response.RecentDocumentResponse;
import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.dto.response.StudentProfileResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.CommentNotificationResponse;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.NodeDiscussion;
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
        private final com.example.flippedclass.repository.LearningSpaceRepository learningSpaceRepository;

        private final QuizQuestionRepository quizQuestionRepository;
        private final CourseDocumentRepository courseDocumentRepository;
        private final com.example.flippedclass.repository.NodeDiscussionRepository nodeDiscussionRepository;
        private final com.example.flippedclass.repository.LearningNodeItemRepository learningNodeItemRepository;

        @Override
        @Transactional(readOnly = true)
        public StudentDashboardResponse getDashboard(Long studentId) {
                User user = userRepository.findById(studentId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Student user not found"));

                StudentProfileResponse profile = studentProfileRepository.findByUser_Id(studentId)
                                .map(this::toStudentProfileResponse)
                                .orElse(null);

                List<LearningSpaceMember> memberships = learningSpaceMemberRepository
                                .findByUser_IdOrderByJoinedAtDesc(studentId);
                List<Long> joinedSpaceIds = memberships.stream()
                                .map(member -> member.getLearningSpace().getId())
                                .distinct()
                                .toList();

                List<DashboardLearningSpaceResponse> learningSpaces = new java.util.ArrayList<>();
                List<Long> allSpaceIds = new java.util.ArrayList<>(joinedSpaceIds);

                // Thêm khóa học đã tham gia
                memberships.forEach(member -> {
                        Long spaceId = member.getLearningSpace().getId();
                        List<QuizResponse> quizzes = quizRepository
                                        .findByLearningNode_LearningPath_LearningSpace_IdAndActiveTrue(spaceId).stream()
                                        .map(q -> QuizResponse.builder()
                                                        .id(q.getId())
                                                        .title(q.getTitle())
                                                        .durationMinutes(q.getDurationMinutes())
                                                        .isCompleted(quizAttemptRepository.existsByQuizIdAndStudent_Id(
                                                                        q.getId(), studentId))
                                                        .build())
                                        .collect(Collectors.toList());
                        learningSpaces.add(DashboardLearningSpaceResponse.from(member, quizzes));
                });

                // Thêm khóa học PUBLIC chưa tham gia
                List<com.example.flippedclass.entity.LearningSpace> publicSpaces = learningSpaceRepository
                                .findByVisibilityAndStatus(com.example.flippedclass.enums.VisibilityType.PUBLIC,
                                                com.example.flippedclass.enums.LearningSpaceStatus.ACTIVE);

                for (com.example.flippedclass.entity.LearningSpace space : publicSpaces) {
                        if (!joinedSpaceIds.contains(space.getId())) {
                                allSpaceIds.add(space.getId());
                                List<QuizResponse> quizzes = quizRepository
                                                .findByLearningNode_LearningPath_LearningSpace_IdAndActiveTrue(
                                                                space.getId())
                                                .stream()
                                                .map(q -> QuizResponse.builder()
                                                                .id(q.getId())
                                                                .title(q.getTitle())
                                                                .durationMinutes(q.getDurationMinutes())
                                                                .isCompleted(quizAttemptRepository
                                                                                .existsByQuizIdAndStudent_Id(q.getId(),
                                                                                                studentId))
                                                                .build())
                                                .collect(Collectors.toList());
                                learningSpaces.add(DashboardLearningSpaceResponse.fromPublicSpace(space, quizzes));
                        }
                }

                List<RecentDocumentResponse> recentDocuments = allSpaceIds.isEmpty()
                                ? List.of()
                                : learningNodeItemRepository
                                                .findRecentItemsBySpaceIds(
                                                                allSpaceIds,
                                                                com.example.flippedclass.enums.ItemType.PDF,
                                                                org.springframework.data.domain.PageRequest.of(0, 8))
                                                .stream()
                                                .map(RecentDocumentResponse::from)
                                                .toList();

                List<NodeDiscussion> replies = nodeDiscussionRepository.findRepliesToUser(studentId);
                List<CommentNotificationResponse> commentNotifications = replies.stream()
                                .map(reply -> {
                                        String spaceName = "Learning Space";
                                        Long spaceId = null;
                                        if (reply.getLearningNode() != null
                                                        && reply.getLearningNode().getLearningPath() != null
                                                        && reply.getLearningNode().getLearningPath()
                                                                        .getLearningSpace() != null) {
                                                spaceName = reply.getLearningNode().getLearningPath().getLearningSpace()
                                                                .getName();
                                                spaceId = reply.getLearningNode().getLearningPath().getLearningSpace()
                                                                .getId();
                                        }

                                        String replierName = "Someone";
                                        if (reply.getUser() != null) {
                                                replierName = (reply.getUser().getFullName() != null
                                                                && !reply.getUser().getFullName().trim().isEmpty())
                                                                                ? reply.getUser().getFullName()
                                                                                : reply.getUser().getUsername();
                                        }

                                        String replyContent = reply.getContent() != null ? reply.getContent() : "";
                                        String notificationText = replyContent;

                                        return CommentNotificationResponse.builder()
                                                        .id(reply.getId())
                                                        .nodeId(reply.getLearningNode() != null
                                                                        ? reply.getLearningNode().getId()
                                                                        : null)
                                                        .replierName(replierName)
                                                        .content(notificationText)
                                                        .spaceName(spaceName)
                                                        .spaceId(spaceId)
                                                        .createdAt(reply.getCreatedAt())
                                                        .build();
                                })
                                .filter(notification -> notification.getSpaceId() != null
                                                && allSpaceIds.contains(notification.getSpaceId()))
                                .toList();

                return new StudentDashboardResponse(
                                DashboardUserResponse.from(user),
                                profile,
                                learningSpaces,
                                recentDocuments,
                                commentNotifications);
        }

        @Override
        @Transactional
        public StudentDashboardResponse updateProfile(Long studentId, UpdateStudentProfileRequest request) {
                User user = userRepository.findById(studentId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Student user not found"));

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
                                .filter(existing -> profile.getId() == null
                                                || !existing.getId().equals(profile.getId()))
                                .ifPresent(existing -> {
                                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                        "Student code is already taken");
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
                if (value == null)
                        return null;
                String trimmed = value.trim();
                return trimmed.isEmpty() ? null : trimmed;
        }

        private com.example.flippedclass.dto.response.StudentProfileResponse toStudentProfileResponse(
                        com.example.flippedclass.entity.StudentProfile profile) {
                if (profile == null)
                        return null;
                return com.example.flippedclass.dto.response.StudentProfileResponse.builder()
                                .id(profile.getId())
                                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                                .studentCode(profile.getStudentCode())
                                .major(profile.getMajor())
                                .className(profile.getClassName())
                                .enrollmentYear(profile.getEnrollmentYear())
                                .phoneNumber(profile.getPhoneNumber())
                                .bio(profile.getBio())
                                .build();
        }
}
