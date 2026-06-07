package com.example.flippedclass.validation;

import com.example.flippedclass.entity.Quiz;
import com.example.flippedclass.entity.QuizQuestion;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.repository.QuizAttemptRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuizValidator {

    private final QuizAttemptRepository attemptRepository;

    public QuizValidator(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    public void validateOwnership(Long currentUserId, Quiz quiz, String action) {
        if (quiz.getLecturer() == null || !quiz.getLecturer().getId().equals(currentUserId)) {
            throw new BusinessException("Access denied: you can only " + action + " your own quiz.");
        }
    }

    public void validateDeletable(Long quizId) {
        long attemptsCount = attemptRepository.countByQuizId(quizId);
        if (attemptsCount > 0) {
            throw new BusinessException("Cannot delete quiz because it has already been attempted by students.");
        }
    }

    public void validateAttemptAnswers(List<QuizQuestion> questions, Map<Long, String> answers) {
        if (questions.isEmpty()) {
            throw new BusinessException("Quiz has no questions");
        }

        Set<Long> validQuestionIds = questions.stream()
                .map(QuizQuestion::getId)
                .collect(Collectors.toSet());

        for (Long answerQuestionId : answers.keySet()) {
            if (!validQuestionIds.contains(answerQuestionId)) {
                throw new BusinessException("Unknown question id in answers: " + answerQuestionId);
            }
        }
    }
}
