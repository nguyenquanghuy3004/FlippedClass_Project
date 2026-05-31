package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.QuizAttemptResponse;
import com.example.flippedclass.dto.response.QuizQuestionResponse;
import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.dto.response.QuizStatisticsResponse;
import com.example.flippedclass.dto.request.CreateQuizQuestionRequest;
import com.example.flippedclass.dto.request.CreateQuizRequest;
import com.example.flippedclass.dto.request.SubmitQuizAttemptRequest;
import com.example.flippedclass.dto.request.UpdateQuizRequest;

import java.util.List;

public interface QuizService {
    List<QuizResponse> findByLearningNode(Long nodeId);

    QuizResponse create(Long nodeId, CreateQuizRequest request);

    QuizResponse create(CreateQuizRequest request);

    QuizResponse update(Long nodeId, Long quizId, UpdateQuizRequest request);

    QuizResponse update(Long id, UpdateQuizRequest request);

    QuizResponse getById(Long id);

    QuizResponse getById(Long nodeId, Long quizId);

    List<QuizResponse> getAll();

    void delete(Long nodeId, Long quizId);

    void delete(Long id);

    QuizQuestionResponse addQuestion(Long nodeId, Long quizId, CreateQuizQuestionRequest request);

    QuizQuestionResponse addQuestion(Long quizId, CreateQuizQuestionRequest request);

    List<QuizQuestionResponse> getQuestions(Long nodeId, Long quizId);

    List<QuizQuestionResponse> getQuestions(Long quizId);

    void deleteQuestion(Long questionId);

    QuizAttemptResponse submitAttempt(Long quizId, SubmitQuizAttemptRequest request);

    List<QuizAttemptResponse> getAttempts(Long quizId);

    List<QuizAttemptResponse> getAttempts(Long nodeId, Long quizId);

    QuizStatisticsResponse getStatistics(Long quizId);

    QuizStatisticsResponse getStatistics(Long nodeId, Long quizId);


    List<QuizResponse> getActiveQuizzesByLearningNode(Long learningNodeId);
}
