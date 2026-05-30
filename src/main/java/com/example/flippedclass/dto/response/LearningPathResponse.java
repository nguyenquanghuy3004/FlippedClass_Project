package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class LearningPathResponse {
    private Long id;
    private Long learningSpaceId;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private LearningPathStatus status;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LearningPathResponse from(LearningPath learningPath) {
        LearningPathResponse response = new LearningPathResponse();
        response.id = learningPath.getId();
        response.learningSpaceId = learningPath.getLearningSpaceId();
        response.lecturerId = learningPath.getLecturerId();
        response.lecturerName = learningPath.getLecturer() == null ? null : learningPath.getLecturer().getFullName();
        response.title = learningPath.getTitle();
        response.description = learningPath.getDescription();
        response.status = learningPath.getStatus();
        response.position = learningPath.getPosition();
        response.createdAt = learningPath.getCreatedAt();
        response.updatedAt = learningPath.getUpdatedAt();
        return response;
    }
}
