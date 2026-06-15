package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.FeedbackSummaryRequest;
import com.example.flippedclass.dto.request.SubmitSummaryRequest;
import com.example.flippedclass.dto.response.LessonSummaryResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LessonSummary;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.SummaryStatus;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LessonSummaryRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LessonSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonSummaryServiceImpl implements LessonSummaryService {

    private final LessonSummaryRepository lessonSummaryRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final UserRepository userRepository;


    @Override
    public LessonSummaryResponse submitSummary(SubmitSummaryRequest request) {
        LearningNode learningNode = learningNodeRepository.findById(request.getLearningNodeId())
                .orElseThrow(() -> new RuntimeException("Learning Node not found"));
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Optional<LessonSummary> existingSummaryOpt = lessonSummaryRepository.findByLearningNodeIdAndStudentId(learningNode.getId(), student.getId());
        LessonSummary summary;
        
        if (existingSummaryOpt.isPresent()) {
            summary = existingSummaryOpt.get();
            summary.setSummaryContent(request.getSummaryContent());
            summary.setStatus(SummaryStatus.SUBMITTED);
            summary.setSubmittedAt(LocalDateTime.now());
        } else {
            summary = LessonSummary.builder()
                    .learningNode(learningNode)
                    .student(student)
                    .summaryContent(request.getSummaryContent())
                    .status(SummaryStatus.SUBMITTED)
                    .submittedAt(LocalDateTime.now())
                    .build();
        }

        summary = lessonSummaryRepository.save(summary);

        return mapToResponse(summary);
    }

    @Override
    public LessonSummaryResponse provideFeedback(Long id, FeedbackSummaryRequest request) {
        LessonSummary summary = lessonSummaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson summary not found"));
        
        summary.setLecturerFeedback(request.getLecturerFeedback());
        summary.setStatus(SummaryStatus.REVIEWED);
        summary.setReviewedAt(LocalDateTime.now());
        
        summary = lessonSummaryRepository.save(summary);
        return mapToResponse(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> getSummariesByLearningNode(Long learningNodeId) {
        return lessonSummaryRepository.findByLearningNodeId(learningNodeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> getSummariesByStudent(Long studentId) {
        return lessonSummaryRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LessonSummaryResponse mapToResponse(LessonSummary summary) {
        return LessonSummaryResponse.builder()
                .id(summary.getId())
                .learningNodeId(summary.getLearningNode().getId())
                .studentId(summary.getStudent().getId())
                .studentName(summary.getStudent().getFullName())
                .summaryContent(summary.getSummaryContent())
                .lecturerFeedback(summary.getLecturerFeedback())
                .status(summary.getStatus())
                .submittedAt(summary.getSubmittedAt())
                .reviewedAt(summary.getReviewedAt())
                .build();
    }
}
