package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.*;
import com.example.flippedclass.dto.request.CreateEvaluationSessionRequest;
import com.example.flippedclass.dto.request.CreateInteractionLogRequest;
import com.example.flippedclass.dto.request.SubmitGradeRequest;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.flippedclass.service.EvaluationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationSessionRepository sessionRepository;
    private final EvaluationCriterionRepository criterionRepository;
    private final InteractionLogRepository interactionLogRepository;
    private final GradeEntryRepository gradeEntryRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final LearningPathRepository learningPathRepository;

    public EvaluationServiceImpl(EvaluationSessionRepository sessionRepository,
                                 EvaluationCriterionRepository criterionRepository,
                                 InteractionLogRepository interactionLogRepository,
                                 GradeEntryRepository gradeEntryRepository,
                                 UserRepository userRepository,
                                 StudentProfileRepository studentProfileRepository,
                                 LearningPathRepository learningPathRepository) {
        this.sessionRepository = sessionRepository;
        this.criterionRepository = criterionRepository;
        this.interactionLogRepository = interactionLogRepository;
        this.gradeEntryRepository = gradeEntryRepository;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.learningPathRepository = learningPathRepository;
    }

    @Override
    public EvaluationSessionResponse createSession(CreateEvaluationSessionRequest request) {
        User lecturer = UserServiceImpl.findUser(userRepository, request.getLecturerId());
        LearningPath path = learningPathRepository.findById(request.getLearningPathId())
                .orElseThrow(() -> new NotFoundException("Learning path not found: " + request.getLearningPathId()));

        EvaluationSession session = EvaluationSession.builder()
                .learningPath(path)
                .lecturer(lecturer)
                .title(request.getTitle().trim())
                .gradingStartAt(request.getGradingStartAt())
                .gradingDeadlineAt(request.getGradingDeadlineAt())
                .build();

        for (CreateEvaluationSessionRequest.CriterionItem item : request.getCriteria()) {
            EvaluationCriterion criterion = EvaluationCriterion.builder()
                    .session(session)
                    .name(item.getName().trim())
                    .description(trimToNull(item.getDescription()))
                    .maxScore(item.getMaxScore())
                    .sortOrder(item.getSortOrder() != null ? item.getSortOrder() : 0)
                    .build();
            session.getCriteria().add(criterion);
        }

        return toSessionResponse(sessionRepository.save(session));
    }

    @Override
    public EvaluationSessionResponse getSession(Long id) {
        return toSessionResponse(findSession(id));
    }

    @Override
    public List<EvaluationSessionResponse> getSessionsByLecturer(Long lecturerId) {
        UserServiceImpl.findUser(userRepository, lecturerId);
        return sessionRepository.findByLecturerId(lecturerId).stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Override
    public GradingContextResponse getGradingContext(Long sessionId, Long studentId) {
        EvaluationSession session = findSession(sessionId);
        User student = UserServiceImpl.findUser(userRepository, studentId);

        List<InteractionLogResponse> history = interactionLogRepository
                .findByStudentIdAndLearningPathIdOrderByOccurredAtDesc(studentId, session.getLearningPath().getId())
                .stream()
                .map(this::toInteractionResponse)
                .toList();

        List<GradeEntryResponse> grades = gradeEntryRepository
                .findBySessionIdAndStudentId(sessionId, studentId)
                .stream()
                .map(this::toGradeResponse)
                .toList();

        return GradingContextResponse.builder()
                .student(UserServiceImpl.toResponse(student))
                .session(toSessionResponse(session))
                .interactionHistory(history)
                .existingGrades(grades)
                .build();
    }

    @Override
    public GradeEntryResponse submitGrade(SubmitGradeRequest request) {
        EvaluationSession session = findSession(request.getSessionId());
        User student = UserServiceImpl.findUser(userRepository, request.getStudentId());
        User lecturer = UserServiceImpl.findUser(userRepository, request.getLecturerId());

        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new BusinessException("Lecturer does not own this evaluation session");
        }

        LocalDateTime now = LocalDateTime.now();
        if (session.getGradingStartAt() != null && now.isBefore(session.getGradingStartAt())) {
            throw new BusinessException("Grading has not started yet");
        }
        if (session.getGradingDeadlineAt() != null && now.isAfter(session.getGradingDeadlineAt())) {
            throw new BusinessException("Grading deadline has passed");
        }

        EvaluationCriterion criterion = criterionRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new NotFoundException("Criterion not found: " + request.getCriterionId()));
        if (!criterion.getSession().getId().equals(session.getId())) {
            throw new BusinessException("Criterion does not belong to this session");
        }
        if (criterion.getMaxScore() != null && request.getScore().compareTo(criterion.getMaxScore()) > 0) {
            throw new BusinessException("Score cannot exceed max score " + criterion.getMaxScore());
        }

        GradeEntry entry = gradeEntryRepository
                .findBySessionIdAndStudentIdAndCriterionId(session.getId(), student.getId(), criterion.getId())
                .orElse(GradeEntry.builder()
                        .session(session)
                        .student(student)
                        .criterion(criterion)
                        .lecturer(lecturer)
                        .build());

        entry.setScore(request.getScore());
        entry.setComment(request.getComment() != null ? request.getComment().trim() : null);
        entry.setLecturer(lecturer);
        entry.setGradedAt(now);

        return toGradeResponse(gradeEntryRepository.save(entry));
    }

    @Override
    public List<GradeEntryResponse> getGradesForStudentInSession(Long sessionId, Long studentId) {
        findSession(sessionId);
        UserServiceImpl.findUser(userRepository, studentId);
        return gradeEntryRepository.findBySessionIdAndStudentId(sessionId, studentId).stream()
                .map(this::toGradeResponse)
                .toList();
    }

    @Override
    public InteractionLogResponse addInteractionLog(CreateInteractionLogRequest request) {
        User student = UserServiceImpl.findUser(userRepository, request.getStudentId());
        LearningPath path = learningPathRepository.findById(request.getLearningPathId())
                .orElseThrow(() -> new NotFoundException("Learning path not found: " + request.getLearningPathId()));

        InteractionLog log = InteractionLog.builder()
                .student(student)
                .learningPath(path)
                .interactionType(request.getInteractionType())
                .summary(request.getSummary() != null ? request.getSummary().trim() : null)
                .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
                .build();

        return toInteractionResponse(interactionLogRepository.save(log));
    }

    @Override
    public List<InteractionLogResponse> getInteractionHistory(Long studentId, Long learningPathId) {
        UserServiceImpl.findUser(userRepository, studentId);
        List<InteractionLog> logs = learningPathId != null
                ? interactionLogRepository.findByStudentIdAndLearningPathIdOrderByOccurredAtDesc(studentId, learningPathId)
                : interactionLogRepository.findByStudentIdOrderByOccurredAtDesc(studentId);
        return logs.stream().map(this::toInteractionResponse).toList();
    }

    @Override
    public StudentProfileResponse getStudentProfile(Long userId) {
        User user = UserServiceImpl.findUser(userRepository, userId);
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Student profile not found for user: " + userId));

        return StudentProfileResponse.builder()
                .id(profile.getId())
                .user(UserServiceImpl.toResponse(user))
                .studentCode(profile.getStudentCode())
                .className(profile.getClassName())
                .major(profile.getMajor())
                .enrollmentYear(profile.getEnrollmentYear())
                .build();
    }

    private EvaluationSession findSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evaluation session not found: " + id));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private EvaluationSessionResponse toSessionResponse(EvaluationSession session) {
        List<CriterionResponse> criteria = criterionRepository.findBySessionIdOrderBySortOrderAsc(session.getId())
                .stream()
                .map(c -> CriterionResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .maxScore(c.getMaxScore())
                        .sortOrder(c.getSortOrder())
                        .build())
                .toList();

        return EvaluationSessionResponse.builder()
                .id(session.getId())
                .learningPathId(session.getLearningPath().getId())
                .lecturerId(session.getLecturer().getId())
                .lecturerName(session.getLecturer().getFullName())
                .title(session.getTitle())
                .gradingStartAt(session.getGradingStartAt())
                .gradingDeadlineAt(session.getGradingDeadlineAt())
                .createdAt(session.getCreatedAt())
                .criteria(criteria)
                .build();
    }

    private InteractionLogResponse toInteractionResponse(InteractionLog log) {
        return InteractionLogResponse.builder()
                .id(log.getId())
                .studentId(log.getStudent().getId())
                .learningPathId(log.getLearningPath().getId())
                .interactionType(log.getInteractionType())
                .summary(log.getSummary())
                .occurredAt(log.getOccurredAt())
                .build();
    }

    private GradeEntryResponse toGradeResponse(GradeEntry entry) {
        return GradeEntryResponse.builder()
                .id(entry.getId())
                .sessionId(entry.getSession().getId())
                .studentId(entry.getStudent().getId())
                .studentName(entry.getStudent().getFullName())
                .criterionId(entry.getCriterion().getId())
                .criterionName(entry.getCriterion().getName())
                .score(entry.getScore())
                .comment(entry.getComment())
                .gradedAt(entry.getGradedAt())
                .build();
    }
}
