package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EvaluationSessionResponse {
    private Long id;
    private Long learningPathId;
    private String learningPathTitle;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private LocalDateTime gradingStartAt;
    private LocalDateTime gradingDeadlineAt;
    private LocalDateTime createdAt;
    private List<CriterionResponse> criteria;
}
