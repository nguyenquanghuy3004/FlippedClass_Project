package com.example.flippedclass.service.impl;

import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.NodeProgress;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ProgressStatus;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.NodeProgressRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.ProgressEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProgressEvaluationServiceImpl implements ProgressEvaluationService {

    private final NodeProgressRepository nodeProgressRepository;
    private final UserRepository userRepository;
    private final LearningNodeRepository learningNodeRepository;

    @Override
    @Transactional
    public void evaluateNodeCompletion(Long studentId, Long learningNodeId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        LearningNode node = learningNodeRepository.findById(learningNodeId)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        NodeProgress progress = nodeProgressRepository.findByStudentIdAndLearningNodeId(studentId, learningNodeId)
                .orElse(NodeProgress.builder()
                        .student(student)
                        .learningNode(node)
                        .status(ProgressStatus.NOT_STARTED)
                        .build());

        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());

        nodeProgressRepository.save(progress);
    }
}
