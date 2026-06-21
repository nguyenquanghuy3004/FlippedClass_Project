package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.Quiz;
import com.example.flippedclass.entity.QuizAttempt;
import com.example.flippedclass.entity.QuizQuestion;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.flippedclass.repository.QuizAttemptRepository;
import com.example.flippedclass.repository.QuizQuestionRepository;
import com.example.flippedclass.repository.QuizRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.service.QuizService;
import com.example.flippedclass.validation.QuizValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final com.example.flippedclass.repository.InteractionLogRepository interactionLogRepository;
    private final QuizValidator quizValidator;
    private final com.example.flippedclass.repository.LearningSpaceMemberRepository memberRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                           QuizQuestionRepository questionRepository,
                           QuizAttemptRepository attemptRepository,
                           UserRepository userRepository,
                           LearningNodeRepository learningNodeRepository,
                           com.example.flippedclass.repository.InteractionLogRepository interactionLogRepository,
                           QuizValidator quizValidator,
                           com.example.flippedclass.repository.LearningSpaceMemberRepository memberRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.learningNodeRepository = learningNodeRepository;
        this.interactionLogRepository = interactionLogRepository;
        this.quizValidator = quizValidator;
        this.memberRepository = memberRepository;
    }

    @Override
    public QuizResponse createForCurrentUser(Long currentUserId, CreateQuizRequest request) {
        User lecturer = UserServiceImpl.findUser(userRepository, currentUserId);
        
        // Security check for STUDENTs: Must be a SUPPORTER in at least one space
        if (lecturer.getRoles().stream().noneMatch(r -> r.getName() == com.example.flippedclass.enums.RoleName.MENTOR || r.getName() == com.example.flippedclass.enums.RoleName.ADMIN)) {
            if (!memberRepository.existsByUser_IdAndRole(lecturer.getId(), com.example.flippedclass.enums.MemberRole.SUPPORTER)) {
                throw new IllegalArgumentException("Chỉ những sinh viên được thăng cấp (Supporter) mới có quyền tạo Quiz.");
            }
        }
        
        LearningNode node = learningNodeRepository.findById(request.getLearningNodeId())
                .orElseThrow(() -> new NotFoundException("Learning node not found: " + request.getLearningNodeId()));

        Quiz quiz = Quiz.builder()
                .learningNode(node)
                .lecturer(lecturer)
                .title(request.getTitle().trim())
                .description(trimToNull(request.getDescription()))
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 30)
                .active(request.getActive() == null || request.getActive())
                .passScore(request.getPassScore() != null ? request.getPassScore() : 50)
                .difficulty(request.getDifficulty())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();
        return toResponse(quizRepository.save(quiz));
    }

    @Override
    public QuizResponse updateOwned(Long currentUserId, Long quizId, UpdateQuizRequest request) {
        Quiz quiz = findQuiz(quizId);
        quizValidator.validateOwnership(currentUserId, quiz, "update");
        return applyUpdate(quiz, request);
    }

    @Override
    public void deleteOwned(Long currentUserId, Long quizId) {
        Quiz quiz = findQuiz(quizId);
        quizValidator.validateOwnership(currentUserId, quiz, "delete");
        quizValidator.validateDeletable(quizId);
        questionRepository.deleteByQuizId(quizId);
        quizRepository.delete(quiz);
    }

    @Override
    public QuizQuestionResponse addQuestionOwned(Long currentUserId, Long quizId, CreateQuizQuestionRequest request) {
        Quiz quiz = findQuiz(quizId);
        quizValidator.validateOwnership(currentUserId, quiz, "add question to");
        return buildAndSaveQuestion(quiz, request);
    }

    @Override
    public QuizQuestionResponse updateQuestionOwned(Long currentUserId, Long questionId, CreateQuizQuestionRequest request) {
        QuizQuestion question = findQuestion(questionId);
        quizValidator.validateOwnership(currentUserId, question.getQuiz(), "update question of");
        return applyQuestionUpdate(question, request);
    }

    @Override
    public void deleteQuestionOwned(Long currentUserId, Long questionId) {
        QuizQuestion question = findQuestion(questionId);
        quizValidator.validateOwnership(currentUserId, question.getQuiz(), "delete question of");
        questionRepository.delete(question);
    }

    @Override
    public QuizResponse getById(Long id) {
        return toResponse(findQuiz(id));
    }

    @Override
    public List<QuizResponse> getAll() {
        return quizRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<QuizResponse> getByLecturer(Long lecturerId) {
        return quizRepository.findByLecturer_Id(lecturerId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<QuizQuestionResponse> getQuestions(Long quizId) {
        findQuiz(quizId);
        return questionRepository.findByQuizId(quizId).stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Override
    public QuizAttemptResponse submitAttempt(Long quizId, SubmitQuizAttemptRequest request) {
        if (attemptRepository.existsByQuizIdAndStudent_Id(quizId, request.getStudentId())) {
            throw new BusinessException("Bạn đã nộp bài rồi, không thể làm lại!");
        }

        Quiz quiz = findQuiz(quizId);
        User student = UserServiceImpl.findUser(userRepository, request.getStudentId());

        List<QuizQuestion> questions = questionRepository.findByQuizId(quizId);
        quizValidator.validateAttemptAnswers(questions, request.getAnswers());

        Map<Long, String> answers = request.getAnswers();
        int correct = 0;
        int earnedPoints = 0;
        int totalPoints = 0;

        for (QuizQuestion q : questions) {
            totalPoints += q.getPoints();
            String answer = answers.get(q.getId());
            if (answer != null && answer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                correct++;
                earnedPoints += q.getPoints();
            }
        }

        BigDecimal score = totalPoints == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(earnedPoints * 100.0 / totalPoints).setScale(2, RoundingMode.HALF_UP);

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .totalQuestions(questions.size())
                .correctAnswers(correct)
                .startedAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Create InteractionLog for Lecturer
        if (quiz.getLearningNode() != null && quiz.getLearningNode().getLearningPath() != null) {
            com.example.flippedclass.entity.InteractionLog log = com.example.flippedclass.entity.InteractionLog.builder()
                    .student(student)
                    .learningPath(quiz.getLearningNode().getLearningPath())
                    .interactionType("QUIZ_SUBMIT")
                    .summary("Student " + student.getFullName() + " submitted quiz '" + quiz.getTitle() + "' with score: " + score + "% (" + correct + "/" + questions.size() + ")")
                    .build();
            interactionLogRepository.save(log);
        }

        return toAttemptResponse(savedAttempt);
    }

    @Override
    public List<QuizAttemptResponse> getAttempts(Long quizId) {
        findQuiz(quizId);
        return attemptRepository.findByQuizId(quizId).stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    @Override
    public List<QuizAttemptResponse> getAttemptsByStudent(Long studentId) {
        return attemptRepository.findByStudent_Id(studentId).stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    @Override
    public QuizAttemptResponse getAttemptById(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Attempt not found: " + attemptId));
        return toAttemptResponse(attempt);
    }

    @Override
    public QuizStatisticsResponse getStatistics(Long quizId) {
        Quiz quiz = findQuiz(quizId);
        List<QuizAttempt> attempts = attemptRepository.findByQuizId(quizId);
        long total = attempts.size();

        BigDecimal avg = attemptRepository.averageScoreByQuizId(quizId);
        if (avg == null) {
            avg = BigDecimal.ZERO;
        }

        BigDecimal highest = attempts.stream()
                .map(QuizAttempt::getScore)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal lowest = attempts.stream()
                .map(QuizAttempt::getScore)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        return QuizStatisticsResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalAttempts(total)
                .averageScore(avg.setScale(2, RoundingMode.HALF_UP))
                .highestScore(highest)
                .lowestScore(lowest)
                .build();
    }

    @Override
    public List<QuizResponse> getActiveQuizzesByLearningNode(Long learningNodeId) {
        return quizRepository.findByLearningNode_IdAndActiveTrue(learningNodeId).stream()
                .map(this::toResponse)
                .toList();
    }

    private QuizResponse applyUpdate(Quiz quiz, UpdateQuizRequest request) {
        if (request.getTitle() != null) quiz.setTitle(request.getTitle().trim());
        if (request.getDescription() != null) quiz.setDescription(trimToNull(request.getDescription()));
        if (request.getDurationMinutes() != null) quiz.setDurationMinutes(request.getDurationMinutes());
        if (request.getActive() != null) quiz.setActive(request.getActive());
        if (request.getPassScore() != null) quiz.setPassScore(request.getPassScore());
        if (request.getDifficulty() != null) quiz.setDifficulty(request.getDifficulty());
        if (request.getThumbnailUrl() != null) quiz.setThumbnailUrl(request.getThumbnailUrl());
        return toResponse(quizRepository.save(quiz));
    }

    private QuizQuestionResponse buildAndSaveQuestion(Quiz quiz, CreateQuizQuestionRequest request) {
        QuizQuestion question = QuizQuestion.builder()
                .quiz(quiz)
                .content(request.getContent().trim())
                .options(request.getOptions() != null ? request.getOptions().trim() : "")
                .correctAnswer(request.getCorrectAnswer().trim())
                .points(request.getPoints())
                .questionType(request.getQuestionType() != null ? request.getQuestionType() : "SINGLE_CHOICE")
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        return toQuestionResponse(questionRepository.save(question));
    }

    private QuizQuestionResponse applyQuestionUpdate(QuizQuestion question, CreateQuizQuestionRequest request) {
        if (request.getContent() != null) question.setContent(request.getContent().trim());
        if (request.getOptions() != null) question.setOptions(request.getOptions().trim());
        if (request.getCorrectAnswer() != null) question.setCorrectAnswer(request.getCorrectAnswer().trim());
        if (request.getPoints() != null) question.setPoints(request.getPoints());
        if (request.getQuestionType() != null) question.setQuestionType(request.getQuestionType());
        if (request.getSortOrder() != null) question.setSortOrder(request.getSortOrder());
        return toQuestionResponse(questionRepository.save(question));
    }

    private Quiz findQuiz(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + id));
    }

    private QuizQuestion findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private QuizResponse toResponse(Quiz quiz) {
        long totalAttempts = attemptRepository.countByQuizId(quiz.getId());
        BigDecimal avgScore = attemptRepository.averageScoreByQuizId(quiz.getId());
        double avg = avgScore != null ? avgScore.doubleValue() : 0.0;

        Integer passScore = quiz.getPassScore() != null ? quiz.getPassScore() : 50;
        long passedCount = attemptRepository.findByQuizId(quiz.getId()).stream()
                .filter(a -> a.getScore() != null && a.getScore().compareTo(BigDecimal.valueOf(passScore)) >= 0).count();
        double passRate = totalAttempts > 0 ? (passedCount * 100.0 / totalAttempts) : 0.0;

        Long spaceId = null;
        String spaceName = "Uncategorized";
        if (quiz.getLearningNode() != null && quiz.getLearningNode().getLearningPath() != null
                && quiz.getLearningNode().getLearningPath().getLearningSpace() != null) {
            spaceId = quiz.getLearningNode().getLearningPath().getLearningSpace().getId();
            spaceName = quiz.getLearningNode().getLearningPath().getLearningSpace().getName();
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .learningNodeId(quiz.getLearningNode() != null ? quiz.getLearningNode().getId() : null)
                .courseName(quiz.getLearningNode() != null ? quiz.getLearningNode().getTitle() : null)
                .lecturerId(quiz.getLecturer() != null ? quiz.getLecturer().getId() : null)
                .lecturerName(quiz.getLecturer() != null ? quiz.getLecturer().getFullName() : null)
                .learningSpaceId(spaceId)
                .learningSpaceName(spaceName)
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .durationMinutes(quiz.getDurationMinutes())
                .active(quiz.isActive())
                .createdAt(quiz.getCreatedAt())
                .passScore(quiz.getPassScore())
                .difficulty(quiz.getDifficulty())
                .thumbnailUrl(quiz.getThumbnailUrl())
                .questionCount(questionRepository.findByQuizId(quiz.getId()).size())
                .totalAttempts(totalAttempts)
                .averageScore(avg)
                .passRate(passRate)
                .build();
    }

    private QuizQuestionResponse toQuestionResponse(QuizQuestion q) {
        return QuizQuestionResponse.builder()
                .id(q.getId())
                .quizId(q.getQuiz().getId())
                .content(q.getContent())
                .options(q.getOptions())
                .correctAnswer(q.getCorrectAnswer())
                .points(q.getPoints())
                .questionType(q.getQuestionType())
                .sortOrder(q.getSortOrder())
                .build();
    }

    private QuizAttemptResponse toAttemptResponse(QuizAttempt a) {
        String name = a.getStudent().getFullName();
        if (name == null || name.trim().isEmpty()) {
            name = a.getStudent().getUsername();
        }
        
        return QuizAttemptResponse.builder()
                .id(a.getId())
                .quizId(a.getQuiz().getId())
                .studentId(a.getStudent().getId())
                .studentName(name)
                .score(a.getScore())
                .totalQuestions(a.getTotalQuestions())
                .correctAnswers(a.getCorrectAnswers())
                .startedAt(a.getStartedAt() != null ? a.getStartedAt() : a.getSubmittedAt())
                .submittedAt(a.getSubmittedAt())
                .build();
    }
}
