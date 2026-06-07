package com.example.flippedclass.service.impl;

import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LessonSummaryRepository;
import com.example.flippedclass.repository.QuizAttemptRepository;
import com.example.flippedclass.service.NodeProgressService;
import com.example.flippedclass.service.ProgressEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProgressEvaluationServiceImpl implements ProgressEvaluationService {

    private final LearningNodeRepository nodeRepository;
    private final NodeProgressService nodeProgressService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final LessonSummaryRepository summaryRepository;

    @Override
    @Transactional
    public void evaluateNodeCompletion(Long studentId, Long learningNodeId) {
        LearningNode node = nodeRepository.findById(learningNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found"));

        boolean isQuizPassed = checkQuizRequirement(studentId, node);
        boolean isSummaryPassed = checkSummaryRequirement(studentId, node);

        // Nếu thoả mãn TẤT CẢ các điều kiện yêu cầu của Node
        if (isQuizPassed && isSummaryPassed) {
            nodeProgressService.updateToComplete(studentId, learningNodeId);
        }
    }

    public boolean checkQuizRequirement(Long studentId, LearningNode node) {
        // Nếu Node không có Quiz nào -> Coi như Pass phần Quiz
        if (node.getQuizzes() == null || node.getQuizzes().isEmpty()) {
            return true;
        }
        // Có quiz -> Check xem đã pass >= 50% chưa
        return quizAttemptRepository.existsPassedAttemptForNode(studentId, node.getId(), BigDecimal.valueOf(50));
    }

    public boolean checkSummaryRequirement(Long studentId, LearningNode node) {
        if (!isSummaryRequired(node)) {
            return true;
        }
        // Có yêu cầu -> Check xem đã nộp chưa
        return summaryRepository.findByStudentIdAndLearningNodeId(studentId, node.getId()).isPresent();
    }

    public boolean isSummaryRequired(LearningNode node) {
        // Giả định: Nếu NodeType là LESSON thì bắt buộc nộp Summary.
        return "LESSON".equalsIgnoreCase(node.getNodeType());
    }
}
