package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.FeedbackSummaryRequest;
import com.example.flippedclass.dto.request.SubmitSummaryRequest;
import com.example.flippedclass.dto.response.LessonSummaryResponse;

import java.util.List;

public interface LessonSummaryService {
    LessonSummaryResponse submitSummary(SubmitSummaryRequest request);
    LessonSummaryResponse provideFeedback(Long id, FeedbackSummaryRequest request);
    LessonSummaryResponse getSummaryById(Long id);
    List<LessonSummaryResponse> getSummariesByLearningNode(Long learningNodeId);
    LessonSummaryResponse getMySummaryByNode(Long learningNodeId, Long studentId);
    List<LessonSummaryResponse> getSummariesByStudent(Long studentId);
}
