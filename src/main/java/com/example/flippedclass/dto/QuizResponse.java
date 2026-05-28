package com.example.flippedclass.dto;

import com.example.flippedclass.entity.Quiz;
import java.time.LocalDateTime;

public class QuizResponse {
    private Long id;
    private Long learningNodeId;
    private Long lecturerId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Boolean active;
    private LocalDateTime createdAt;

    public static QuizResponse from(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.id = quiz.getId();
        response.learningNodeId = quiz.getLearningNodeId();
        response.lecturerId = quiz.getLecturerId();
        response.title = quiz.getTitle();
        response.description = quiz.getDescription();
        response.durationMinutes = quiz.getDurationMinutes();
        response.active = quiz.getActive();
        response.createdAt = quiz.getCreatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getLearningNodeId() {
        return learningNodeId;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
