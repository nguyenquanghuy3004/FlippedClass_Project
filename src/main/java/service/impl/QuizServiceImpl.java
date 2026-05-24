package service.impl;

import dto.response.*;
import dto.request.CreateQuizQuestionRequest;
import dto.request.CreateQuizRequest;
import dto.request.SubmitQuizAttemptRequest;
import dto.request.UpdateQuizRequest;
import entity.Quiz;
import entity.QuizAttempt;
import entity.QuizQuestion;
import entity.User;
import exception.BusinessException;
import exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.QuizAttemptRepository;
import repository.QuizQuestionRepository;
import repository.QuizRepository;
import repository.UserRepository;
import service.QuizService;

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

    public QuizServiceImpl(QuizRepository quizRepository,
                           QuizQuestionRepository questionRepository,
                           QuizAttemptRepository attemptRepository,
                           UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuizResponse create(CreateQuizRequest request) {
        User lecturer = UserServiceImpl.findUser(userRepository, request.getLecturerId());

        Quiz quiz = Quiz.builder()
                .learningNodeId(request.getLearningNodeId())
                .lecturer(lecturer)
                .title(request.getTitle().trim())
                .description(trimToNull(request.getDescription()))
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 30)
                .active(request.getActive() == null || request.getActive())
                .build();
        return toResponse(quizRepository.save(quiz));
    }

    @Override
    public QuizResponse update(Long id, UpdateQuizRequest request) {
        Quiz quiz = findQuiz(id);
        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            quiz.setDescription(trimToNull(request.getDescription()));
        }
        if (request.getDurationMinutes() != null) {
            quiz.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getActive() != null) {
            quiz.setActive(request.getActive());
        }
        return toResponse(quizRepository.save(quiz));
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
    public void delete(Long id) {
        questionRepository.deleteByQuizId(id);
        quizRepository.delete(findQuiz(id));
    }

    @Override
    public QuizQuestionResponse addQuestion(Long quizId, CreateQuizQuestionRequest request) {
        Quiz quiz = findQuiz(quizId);

        QuizQuestion question = QuizQuestion.builder()
                .quiz(quiz)
                .content(request.getContent().trim())
                .options(request.getOptions() != null ? request.getOptions().trim() : "")
                .correctAnswer(request.getCorrectAnswer().trim())
                .points(request.getPoints())
                .build();
        return toQuestionResponse(questionRepository.save(question));
    }

    @Override
    public List<QuizQuestionResponse> getQuestions(Long quizId) {
        findQuiz(quizId);
        return questionRepository.findByQuizId(quizId).stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Override
    public void deleteQuestion(Long questionId) {
        questionRepository.delete(questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found: " + questionId)));
    }

    @Override
    public QuizAttemptResponse submitAttempt(Long quizId, SubmitQuizAttemptRequest request) {
        Quiz quiz = findQuiz(quizId);
        User student = UserServiceImpl.findUser(userRepository, request.getStudentId());

        List<QuizQuestion> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) {
            throw new BusinessException("Quiz has no questions");
        }

        Set<Long> validQuestionIds = questions.stream()
                .map(QuizQuestion::getId)
                .collect(Collectors.toSet());

        for (Long answerQuestionId : request.getAnswers().keySet()) {
            if (!validQuestionIds.contains(answerQuestionId)) {
                throw new BusinessException("Unknown question id in answers: " + answerQuestionId);
            }
        }

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
                .submittedAt(LocalDateTime.now())
                .build();

        return toAttemptResponse(attemptRepository.save(attempt));
    }

    @Override
    public List<QuizAttemptResponse> getAttempts(Long quizId) {
        findQuiz(quizId);
        return attemptRepository.findByQuizId(quizId).stream()
                .map(this::toAttemptResponse)
                .toList();
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

    private Quiz findQuiz(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + id));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private QuizResponse toResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .learningNodeId(quiz.getLearningNodeId())
                .lecturerId(quiz.getLecturer().getId())
                .lecturerName(quiz.getLecturer().getFullName())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .durationMinutes(quiz.getDurationMinutes())
                .active(quiz.isActive())
                .createdAt(quiz.getCreatedAt())
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
                .build();
    }

    private QuizAttemptResponse toAttemptResponse(QuizAttempt a) {
        return QuizAttemptResponse.builder()
                .id(a.getId())
                .quizId(a.getQuiz().getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getFullName())
                .score(a.getScore())
                .totalQuestions(a.getTotalQuestions())
                .correctAnswers(a.getCorrectAnswers())
                .submittedAt(a.getSubmittedAt())
                .build();
    }
}
