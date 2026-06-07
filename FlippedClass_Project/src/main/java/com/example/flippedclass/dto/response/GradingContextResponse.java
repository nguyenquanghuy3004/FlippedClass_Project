package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GradingContextResponse {
    private UserResponse student;
    private EvaluationSessionResponse session;
    private List<InteractionLogResponse> interactionHistory;
    private List<GradeEntryResponse> existingGrades;
}
