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


    QuizResponse create(CreateQuizRequest request);

    QuizResponse update(Long id, UpdateQuizRequest request);

    QuizResponse getById(Long id);

    List<QuizResponse> getAll();

    List<QuizResponse> getByLecturer(Long lecturerId);

    void delete(Long id);

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
}
