package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.service.LearningNodeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

import com.example.flippedclass.entity.LearningNodeItem;
import com.example.flippedclass.entity.Quiz;

@Service
public class LearningNodeServiceImpl implements LearningNodeService {

    @Autowired
    private LearningNodeRepository learningNodeRepository;

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Override
    public LearningNodeResponse createLearningNode(Long pathId, CreateLearningNodeRequest request) {
        LearningPath learningPath = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new RuntimeException("Khng tm thy Learning Path"));

        LearningNode node = LearningNode.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .learningPath(learningPath)
                .status("ACTIVE")
                .nodeType(request.getNodeType() != null ? request.getNodeType() : "VIDEO")
                .build();

        LearningNode savedNode = learningNodeRepository.save(node);

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .description(savedNode.getDescription())
                .learningPathId(learningPath.getId())
                .learningSpaceId(learningPath.getLearningSpace() != null ? learningPath.getLearningSpace().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

    @Autowired
    private com.example.flippedclass.repository.LearningNodeItemRepository learningNodeItemRepository;

    @Autowired
    private com.example.flippedclass.repository.QuizRepository quizRepository;

    @Autowired
    private com.example.flippedclass.repository.QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private com.example.flippedclass.repository.QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private com.example.flippedclass.repository.TestCaseRepository testCaseRepository;

    @Autowired
    private com.example.flippedclass.repository.SharedSolutionRepository sharedSolutionRepository;

    @Autowired
    private com.example.flippedclass.repository.NodeProgressRepository nodeProgressRepository;

    @Autowired
    private com.example.flippedclass.repository.NodeDiscussionRepository nodeDiscussionRepository;

    @Autowired
    private com.example.flippedclass.repository.NodeCommentRepository nodeCommentRepository;

    @Autowired
    private com.example.flippedclass.repository.LessonSummaryRepository lessonSummaryRepository;

    @Autowired
    private com.example.flippedclass.repository.GroupActivityRepository groupActivityRepository;

    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bai hoc"));
        
        // 1. Delete quiz attempts and quiz questions for all quizzes of this node
        if (node.getQuizzes() != null) {
            for (Quiz quiz : node.getQuizzes()) {
                quizAttemptRepository.deleteByQuizId(quiz.getId());
                quizQuestionRepository.deleteByQuizId(quiz.getId());
            }
        }

        // 2. Delete test cases
        testCaseRepository.deleteByLearningNodeId(nodeId);

        // 3. Delete shared solutions
        sharedSolutionRepository.deleteByLearningNodeId(nodeId);

        // 4. Delete group activities (and their cascaded groups)
        groupActivityRepository.deleteByLearningNodeId(nodeId);

        // 5. Delete node progress
        nodeProgressRepository.deleteByLearningNodeId(nodeId);

        // 6. Delete node discussions (derived delete loads and deletes with cascade replies)
        nodeDiscussionRepository.deleteByLearningNodeId(nodeId);

        // 7. Delete node comments safely (replies first, then parents)
        List<com.example.flippedclass.entity.NodeComment> comments = nodeCommentRepository.findByLearningNodeIdOrderByCreatedAtAsc(nodeId);
        if (comments != null && !comments.isEmpty()) {
            for (com.example.flippedclass.entity.NodeComment c : comments) {
                if (c.getParentComment() != null) {
                    nodeCommentRepository.delete(c);
                }
            }
            for (com.example.flippedclass.entity.NodeComment c : comments) {
                if (c.getParentComment() == null) {
                    nodeCommentRepository.delete(c);
                }
            }
        }

        // 8. Delete lesson summaries
        lessonSummaryRepository.deleteByLearningNodeId(nodeId);

        // 9. Explicitly delete items
        if (node.getItems() != null && !node.getItems().isEmpty()) {
            learningNodeItemRepository.deleteAllInBatch(node.getItems());
        }

        // 10. Explicitly delete quizzes
        if (node.getQuizzes() != null && !node.getQuizzes().isEmpty()) {
            quizRepository.deleteAllInBatch(node.getQuizzes());
        }
        
        learningNodeRepository.delete(node);
    }

    @Override
    @Transactional
    public LearningNodeResponse updateLearningNode(Long nodeId, CreateLearningNodeRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Khng tm thy bi hc"));

        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());


        if (request.getNodeType() != null) {
            node.setNodeType(request.getNodeType());
        }

        LearningNode savedNode = learningNodeRepository.save(node);

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .description(savedNode.getDescription())
                .learningPathId(savedNode.getLearningPath() != null ? savedNode.getLearningPath().getId() : null)
                .learningSpaceId((savedNode.getLearningPath() != null && savedNode.getLearningPath().getLearningSpace() != null) ? savedNode.getLearningPath().getLearningSpace().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

}
