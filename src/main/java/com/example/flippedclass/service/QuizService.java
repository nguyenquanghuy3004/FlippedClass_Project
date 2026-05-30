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

    public List<QuizResponse> findByLearningNode(Long nodeId);
    QuizResponse create(CreateQuizRequest request);

    QuizResponse update(Long id, UpdateQuizRequest request);

    QuizResponse getById(Long id);

    List<QuizResponse> getAll();

    void delete(Long id);

    QuizQuestionResponse addQuestion(Long quizId, CreateQuizQuestionRequest request);

    List<QuizQuestionResponse> getQuestions(Long quizId);

    void deleteQuestion(Long questionId);

    QuizAttemptResponse submitAttempt(Long quizId, SubmitQuizAttemptRequest request);

    List<QuizAttemptResponse> getAttempts(Long quizId);

    QuizStatisticsResponse getStatistics(Long quizId);

    List<QuizResponse> getActiveQuizzesByLearningNode(Long learningNodeId);
}
