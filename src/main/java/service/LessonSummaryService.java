package service;

import dto.request.FeedbackSummaryRequest;
import dto.request.SubmitSummaryRequest;
import dto.response.LessonSummaryResponse;

import java.util.List;

public interface LessonSummaryService {
    LessonSummaryResponse submitSummary(SubmitSummaryRequest request);
    LessonSummaryResponse provideFeedback(Long id, FeedbackSummaryRequest request);
    List<LessonSummaryResponse> getSummariesByLearningNode(Long learningNodeId);
    List<LessonSummaryResponse> getSummariesByStudent(Long studentId);
}
