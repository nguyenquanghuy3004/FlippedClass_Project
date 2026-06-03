package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.Quiz;
import lombok.Getter;

@Getter
public class DashboardQuizResponse {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;

    public static DashboardQuizResponse from(Quiz quiz, Integer totalQuestions) {
        DashboardQuizResponse response = new DashboardQuizResponse();
        response.id = quiz.getId();
        response.title = quiz.getTitle();
        response.description = quiz.getDescription();
        response.durationMinutes = quiz.getDurationMinutes();
        response.totalQuestions = totalQuestions;
        return response;
    }
}
