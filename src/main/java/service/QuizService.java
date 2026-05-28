package service;

import dto.response.*;
import dto.request.CreateQuizQuestionRequest;
import dto.request.CreateQuizRequest;
import dto.request.SubmitQuizAttemptRequest;
import dto.request.UpdateQuizRequest;

import java.util.List;

public interface QuizService {

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
