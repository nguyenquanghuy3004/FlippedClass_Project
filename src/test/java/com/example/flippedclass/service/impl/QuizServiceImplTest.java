package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.Quiz;
import com.example.flippedclass.entity.QuizAttempt;
import com.example.flippedclass.entity.QuizQuestion;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.QuizAttemptRepository;
import com.example.flippedclass.repository.QuizQuestionRepository;
import com.example.flippedclass.repository.QuizRepository;
import com.example.flippedclass.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.flippedclass.validation.QuizValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuizQuestionRepository questionRepository;
    @Mock
    private QuizAttemptRepository attemptRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LearningNodeRepository learningNodeRepository;
    @Mock
    private com.example.flippedclass.repository.LearningSpaceMemberRepository memberRepository;
    @Mock
    private QuizValidator quizValidator;

    @InjectMocks
    private QuizServiceImpl quizService;

    private User student;
    private User lecturer;
    private LearningNode learningNode;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setFullName("Student User");

        lecturer = new User();
        lecturer.setId(2L);
        lecturer.setFullName("Lecturer User");

        learningNode = new LearningNode();
        learningNode.setId(1L);
        learningNode.setTitle("Spring Boot Basics");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Spring Boot Quiz");
        quiz.setLecturer(lecturer);
        quiz.setLearningNode(learningNode);
        quiz.setPassScore(60);
    }

    // --- Create Quiz Tests ---

    @Test
    void createQuiz_Success() {
        CreateQuizRequest request = new CreateQuizRequest();
        request.setLecturerId(2L);
        request.setLearningNodeId(1L);
        request.setTitle("New Quiz");
        request.setPassScore(50);

        when(userRepository.findById(2L)).thenReturn(Optional.of(lecturer));
        when(memberRepository.existsByUser_IdAndRole(2L, com.example.flippedclass.enums.MemberRole.SUPPORTER)).thenReturn(true);
        when(learningNodeRepository.findById(1L)).thenReturn(Optional.of(learningNode));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(i -> {
            Quiz saved = (Quiz) i.getArguments()[0];
            saved.setId(10L);
            return saved;
        });

        QuizResponse response = quizService.createForCurrentUser(2L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("New Quiz", response.getTitle());
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    void createQuiz_LearningNodeNotFound() {
        CreateQuizRequest request = new CreateQuizRequest();
        request.setLecturerId(2L);
        request.setLearningNodeId(999L); // Invalid ID

        when(userRepository.findById(2L)).thenReturn(Optional.of(lecturer));
        when(memberRepository.existsByUser_IdAndRole(2L, com.example.flippedclass.enums.MemberRole.SUPPORTER)).thenReturn(true);
        when(learningNodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> quizService.createForCurrentUser(2L, request));
    }

    // --- Submit Attempt Tests ---

    @Test
    void submitAttempt_PerfectScore() {
        // Setup Quiz and Questions
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        QuizQuestion q1 = QuizQuestion.builder().id(1L).quiz(quiz).points(10).correctAnswer("A").build();
        QuizQuestion q2 = QuizQuestion.builder().id(2L).quiz(quiz).points(10).correctAnswer("B").build();
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(q1, q2));

        // Submit correct answers
        SubmitQuizAttemptRequest request = new SubmitQuizAttemptRequest();
        request.setStudentId(1L);
        Map<Long, String> answers = new HashMap<>();
        answers.put(1L, "A");
        answers.put(2L, "B");
        request.setAnswers(answers);

        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> {
            QuizAttempt attempt = (QuizAttempt) i.getArguments()[0];
            attempt.setId(100L);
            return attempt;
        });

        QuizAttemptResponse response = quizService.submitAttempt(1L, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getScore());
        assertEquals(2, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
    }

    @Test
    void submitAttempt_PartialScore() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        QuizQuestion q1 = QuizQuestion.builder().id(1L).quiz(quiz).points(10).correctAnswer("A").build();
        QuizQuestion q2 = QuizQuestion.builder().id(2L).quiz(quiz).points(30).correctAnswer("B").build();
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(q1, q2));

        // Submit 1 correct, 1 wrong (Total points = 40. Earned = 10 -> Score = 25%)
        SubmitQuizAttemptRequest request = new SubmitQuizAttemptRequest();
        request.setStudentId(1L);
        Map<Long, String> answers = new HashMap<>();
        answers.put(1L, "A"); // Correct
        answers.put(2L, "C"); // Wrong
        request.setAnswers(answers);

        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> {
            QuizAttempt attempt = (QuizAttempt) i.getArguments()[0];
            attempt.setId(100L);
            return attempt;
        });

        QuizAttemptResponse response = quizService.submitAttempt(1L, request);

        assertEquals(new BigDecimal("25.00"), response.getScore());
        assertEquals(1, response.getCorrectAnswers());
    }

    @Test
    void submitAttempt_ZeroScore_WithEmptyAnswers() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        QuizQuestion q1 = QuizQuestion.builder().id(1L).quiz(quiz).points(10).correctAnswer("A").build();
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1));

        SubmitQuizAttemptRequest request = new SubmitQuizAttemptRequest();
        request.setStudentId(1L);
        request.setAnswers(new HashMap<>()); // Empty answers

        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> {
            QuizAttempt attempt = (QuizAttempt) i.getArguments()[0];
            attempt.setId(100L);
            return attempt;
        });

        QuizAttemptResponse response = quizService.submitAttempt(1L, request);

        assertEquals(new BigDecimal("0.00"), response.getScore());
        assertEquals(0, response.getCorrectAnswers());
    }

    @Test
    void submitAttempt_InvalidQuestionId() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        QuizQuestion q1 = QuizQuestion.builder().id(1L).quiz(quiz).points(10).correctAnswer("A").build();
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1));

        SubmitQuizAttemptRequest request = new SubmitQuizAttemptRequest();
        request.setStudentId(1L);
        Map<Long, String> answers = new HashMap<>();
        answers.put(99L, "A"); // ID 99 does not exist in this quiz
        request.setAnswers(answers);

        doThrow(new BusinessException("Unknown question id in answers: 999"))
            .when(quizValidator).validateAttemptAnswers(any(), any());

        BusinessException exception = assertThrows(BusinessException.class, () -> quizService.submitAttempt(1L, request));
        assertTrue(exception.getMessage().contains("Unknown question id"));
    }

    // --- Statistics Tests ---

    @Test
    void getStatistics_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizAttempt a1 = QuizAttempt.builder().score(new BigDecimal("100.00")).build();
        QuizAttempt a2 = QuizAttempt.builder().score(new BigDecimal("50.00")).build();
        QuizAttempt a3 = QuizAttempt.builder().score(new BigDecimal("0.00")).build();

        when(attemptRepository.findByQuizId(1L)).thenReturn(Arrays.asList(a1, a2, a3));
        when(attemptRepository.averageScoreByQuizId(1L)).thenReturn(50.00);

        QuizStatisticsResponse stats = quizService.getStatistics(1L);

        assertEquals(3L, stats.getTotalAttempts());
        assertEquals(new BigDecimal("50.00"), stats.getAverageScore());
        assertEquals(new BigDecimal("100.00"), stats.getHighestScore());
        assertEquals(new BigDecimal("0.00"), stats.getLowestScore());
    }

    @Test
    void getStatistics_EmptyAttempts() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(attemptRepository.findByQuizId(1L)).thenReturn(Collections.emptyList());
        when(attemptRepository.averageScoreByQuizId(1L)).thenReturn(null);

        QuizStatisticsResponse stats = quizService.getStatistics(1L);

        assertEquals(0L, stats.getTotalAttempts());
        assertEquals(new BigDecimal("0.00"), stats.getAverageScore());
        assertEquals(new BigDecimal("0"), stats.getHighestScore());
        assertEquals(new BigDecimal("0"), stats.getLowestScore());
    }
}
