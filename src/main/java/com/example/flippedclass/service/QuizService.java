package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;
import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;

import java.util.List;

public interface QuizService {

    QuizResponse createForCurrentUser(Long currentUserId, CreateQuizRequest request);
    QuizResponse updateOwned(Long currentUserId, Long quizId, UpdateQuizRequest request);
    void deleteOwned(Long currentUserId, Long quizId);
    QuizQuestionResponse addQuestionOwned(Long currentUserId, Long quizId, CreateQuizQuestionRequest request);
    QuizQuestionResponse updateQuestionOwned(Long currentUserId, Long questionId, CreateQuizQuestionRequest request);
    void deleteQuestionOwned(Long currentUserId, Long questionId);

    QuizResponse getById(Long id);
    List<QuizResponse> getAll();
    List<QuizResponse> getByLecturer(Long lecturerId);
    QuizQuestionResponse addQuestion(Long quizId, CreateQuizQuestionRequest request);
    List<QuizQuestionResponse> getQuestions(Long quizId);
    QuizQuestionResponse updateQuestion(Long questionId, CreateQuizQuestionRequest request);
    void deleteQuestion(Long questionId);
    QuizAttemptResponse submitAttempt(Long quizId, SubmitQuizAttemptRequest request);
    List<QuizAttemptResponse> getAttempts(Long quizId);
    List<QuizAttemptResponse> getAttemptsByStudent(Long studentId);
    QuizAttemptResponse getAttemptById(Long attemptId);
    QuizStatisticsResponse getStatistics(Long quizId);
    List<QuizResponse> getActiveQuizzesByLearningNode(Long learningNodeId);

    QuizResponse create(CreateQuizRequest request);
    QuizResponse update(Long id, UpdateQuizRequest request);
    void delete(Long id);
}
