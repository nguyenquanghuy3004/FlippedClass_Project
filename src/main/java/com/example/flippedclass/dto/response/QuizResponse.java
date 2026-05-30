package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.Quiz;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
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
}
