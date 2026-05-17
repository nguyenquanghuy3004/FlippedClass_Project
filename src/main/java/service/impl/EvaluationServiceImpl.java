package service.impl;

import dto.response.*;
import dto.request.CreateEvaluationSessionRequest;
import dto.request.CreateInteractionLogRequest;
import dto.request.SubmitGradeRequest;
import entity.*;
import entity.enums.UserRole;
import exception.BusinessException;
import exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;
import service.EvaluationService;

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

    public EvaluationServiceImpl(EvaluationSessionRepository sessionRepository,
                                 EvaluationCriterionRepository criterionRepository,
                                 InteractionLogRepository interactionLogRepository,
                                 GradeEntryRepository gradeEntryRepository,
                                 UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.criterionRepository = criterionRepository;
        this.interactionLogRepository = interactionLogRepository;
        this.gradeEntryRepository = gradeEntryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EvaluationSessionResponse createSession(CreateEvaluationSessionRequest request) {
        User lecturer = UserServiceImpl.findUser(userRepository, request.getLecturerId());
        UserServiceImpl.requireRole(lecturer, UserRole.LECTURER);

        EvaluationSession session = EvaluationSession.builder()
                .title(request.getTitle().trim())
                .courseName(request.getCourseName().trim())
                .lecturer(lecturer)
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
        UserServiceImpl.requireRole(student, UserRole.STUDENT);

        List<InteractionLogResponse> history = interactionLogRepository
                .findByStudentIdAndCourseNameOrderByOccurredAtDesc(studentId, session.getCourseName())
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
        UserServiceImpl.requireRole(student, UserRole.STUDENT);
        UserServiceImpl.requireRole(lecturer, UserRole.LECTURER);

        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new BusinessException("Lecturer does not own this evaluation session");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getGradingStartAt())) {
            throw new BusinessException("Grading has not started yet");
        }
        if (now.isAfter(session.getGradingDeadlineAt())) {
            throw new BusinessException("Grading deadline has passed");
        }

        EvaluationCriterion criterion = criterionRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new NotFoundException("Criterion not found: " + request.getCriterionId()));
        if (!criterion.getSession().getId().equals(session.getId())) {
            throw new BusinessException("Criterion does not belong to this session");
        }
        if (request.getScore().compareTo(criterion.getMaxScore()) > 0) {
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
        entry.setComment(request.getComment().trim());
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
        UserServiceImpl.requireRole(student, UserRole.STUDENT);

        InteractionLog log = InteractionLog.builder()
                .student(student)
                .courseName(request.getCourseName().trim())
                .type(request.getType())
                .summary(request.getSummary().trim())
                .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
                .build();

        return toInteractionResponse(interactionLogRepository.save(log));
    }

    @Override
    public List<InteractionLogResponse> getInteractionHistory(Long studentId, String courseName) {
        UserServiceImpl.findUser(userRepository, studentId);
        List<InteractionLog> logs = courseName != null && !courseName.isBlank()
                ? interactionLogRepository.findByStudentIdAndCourseNameOrderByOccurredAtDesc(studentId, courseName.trim())
                : interactionLogRepository.findByStudentIdOrderByOccurredAtDesc(studentId);
        return logs.stream().map(this::toInteractionResponse).toList();
    }

    @Override
    public StudentProfileResponse getStudentProfile(Long studentId) {
        User student = UserServiceImpl.findUser(userRepository, studentId);
        UserServiceImpl.requireRole(student, UserRole.STUDENT);

        List<InteractionLogResponse> interactions = interactionLogRepository
                .findByStudentIdOrderByOccurredAtDesc(studentId)
                .stream()
                .limit(20)
                .map(this::toInteractionResponse)
                .toList();

        List<GradeEntryResponse> grades = gradeEntryRepository.findByStudentId(studentId).stream()
                .map(this::toGradeResponse)
                .toList();

        return StudentProfileResponse.builder()
                .student(UserServiceImpl.toResponse(student))
                .recentInteractions(interactions)
                .gradeHistory(grades)
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
                .title(session.getTitle())
                .courseName(session.getCourseName())
                .lecturerId(session.getLecturer().getId())
                .lecturerName(session.getLecturer().getFullName())
                .gradingStartAt(session.getGradingStartAt())
                .gradingDeadlineAt(session.getGradingDeadlineAt())
                .criteria(criteria)
                .build();
    }

    private InteractionLogResponse toInteractionResponse(InteractionLog log) {
        return InteractionLogResponse.builder()
                .id(log.getId())
                .studentId(log.getStudent().getId())
                .courseName(log.getCourseName())
                .type(log.getType())
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
