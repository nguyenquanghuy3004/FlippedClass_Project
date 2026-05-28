package service.impl;

import dto.request.FeedbackSummaryRequest;
import dto.request.SubmitSummaryRequest;
import dto.response.LessonSummaryResponse;
import entity.LearningNode;
import entity.LessonSummary;
import entity.User;
import enums.SummaryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.LearningNodeRepository;
import repository.LessonSummaryRepository;
import repository.UserRepository;
import service.LessonSummaryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LessonSummaryServiceImpl implements LessonSummaryService {

    private final LessonSummaryRepository lessonSummaryRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final UserRepository userRepository;

    public LessonSummaryServiceImpl(LessonSummaryRepository lessonSummaryRepository,
                                    LearningNodeRepository learningNodeRepository,
                                    UserRepository userRepository) {
        this.lessonSummaryRepository = lessonSummaryRepository;
        this.learningNodeRepository = learningNodeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LessonSummaryResponse submitSummary(SubmitSummaryRequest request) {
        LearningNode learningNode = learningNodeRepository.findById(request.getLearningNodeId())
                .orElseThrow(() -> new RuntimeException("Learning Node not found"));
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        LessonSummary summary = LessonSummary.builder()
                .learningNode(learningNode)
                .student(student)
                .summaryContent(request.getSummaryContent())
                .status(SummaryStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();

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
