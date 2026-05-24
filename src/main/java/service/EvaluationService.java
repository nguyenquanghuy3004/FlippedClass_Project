package service;

import dto.response.*;
import dto.request.CreateEvaluationSessionRequest;
import dto.request.CreateInteractionLogRequest;
import dto.request.SubmitGradeRequest;

import java.util.List;

public interface EvaluationService {

    EvaluationSessionResponse createSession(CreateEvaluationSessionRequest request);

    EvaluationSessionResponse getSession(Long id);

    List<EvaluationSessionResponse> getSessionsByLecturer(Long lecturerId);

    GradingContextResponse getGradingContext(Long sessionId, Long studentId);

    GradeEntryResponse submitGrade(SubmitGradeRequest request);

    List<GradeEntryResponse> getGradesForStudentInSession(Long sessionId, Long studentId);

    InteractionLogResponse addInteractionLog(CreateInteractionLogRequest request);

    List<InteractionLogResponse> getInteractionHistory(Long studentId, Long learningPathId);

    StudentProfileResponse getStudentProfile(Long userId);
}
