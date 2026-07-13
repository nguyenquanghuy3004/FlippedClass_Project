package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.*;
import com.example.flippedclass.dto.request.CreateEvaluationSessionRequest;
import com.example.flippedclass.dto.request.CreateInteractionLogRequest;
import com.example.flippedclass.dto.request.SubmitGradeRequest;

import java.util.List;

public interface EvaluationService {

    EvaluationSessionResponse createSession(CreateEvaluationSessionRequest request);

    EvaluationSessionResponse getSession(Long id);

    List<EvaluationSessionResponse> getSessionsByLecturer(Long lecturerId);

    GradingContextResponse getGradingContext(Long sessionId, Long studentId);

    GradeEntryResponse submitGrade(SubmitGradeRequest request);

    List<GradeEntryResponse> getGradesForStudentInSession(Long sessionId, Long studentId);

    List<GradeEntryResponse> getGradesForStudent(Long studentId);

    List<LearningPathResponse> getLearningPathsForLecturer(Long lecturerId);

    List<UserResponse> getStudentsForSession(Long sessionId);

    InteractionLogResponse addInteractionLog(CreateInteractionLogRequest request);

    List<InteractionLogResponse> getInteractionHistory(Long studentId, Long learningPathId);

    StudentProfileResponse getStudentProfile(Long userId);
}
