package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.entity.NodeConnection;
import com.example.flippedclass.repository.LearningNodeItemRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.repository.NodeConnectionRepository;
import com.example.flippedclass.repository.QuizRepository;
import com.example.flippedclass.service.LearningNodeService;
import jakarta.persistence.EntityManager;
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

    @Autowired
    private NodeConnectionRepository nodeConnectionRepository;
    @Autowired
    private LearningNodeItemRepository learningNodeItemRepository;

    @Autowired
    private QuizRepository quizRepository;

    @jakarta.persistence.PersistenceContext
    private EntityManager entityManager;

    @Override
    public LearningNodeResponse createLearningNode(Long pathId, CreateLearningNodeRequest request) {
        LearningPath learningPath = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Learning Path"));

        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            List<LearningNode> existingNodes = learningNodeRepository.findByLearningPathId(pathId);
            displayOrder = existingNodes.size() + 1;
        }

        LearningNode node = LearningNode.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .learningPath(learningPath)
                .status("ACTIVE")
                .nodeType(request.getNodeType() != null ? request.getNodeType() : "VIDEO")
                .displayOrder(displayOrder)
                .estimatedMinutes(request.getEstimatedMinutes() != null ? request.getEstimatedMinutes() : 15) // default 15 minutes
                .build();

        LearningNode savedNode = learningNodeRepository.save(node);

        if (request.getPrerequisiteNodeId() != null && request.getPrerequisiteNodeId() > 0) {
            LearningNode sourceNode = learningNodeRepository.findById(request.getPrerequisiteNodeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Prerequisite node"));
            NodeConnection connection = NodeConnection.builder()
                    .learningPath(learningPath)
                    .sourceNode(sourceNode)
                    .targetNode(savedNode)
                    .conditionType("COMPLETION")
                    .build();
            nodeConnectionRepository.save(connection);
        }

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .description(savedNode.getDescription())
                .learningPathId(learningPath.getId())
                .learningSpaceId(learningPath.getLearningSpace() != null ? learningPath.getLearningSpace().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .isOptional(savedNode.getIsOptional())
                .content(savedNode.getContent())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }


    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài học"));
        
        // KIỂM TRA ĐIỀU KIỆN CHẶN XÓA (Restrict Delete)
        // 1. Kiểm tra xem có học sinh nào đã làm bài Quiz chưa
        Long attemptCount = entityManager.createQuery("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.learningNode.id = :nodeId", Long.class)
                .setParameter("nodeId", nodeId).getSingleResult();
        
        // 2. Kiểm tra xem có học sinh nào đã hoàn thành bài học này chưa
        Long progressCount = entityManager.createQuery("SELECT COUNT(np) FROM NodeProgress np WHERE np.learningNode.id = :nodeId", Long.class)
                .setParameter("nodeId", nodeId).getSingleResult();

        if (attemptCount > 0 || progressCount > 0) {
            throw new IllegalArgumentException("Cannot delete this lesson because students have already participated. Please archive the module instead.");
        }
        
        // Break self-referencing relationships first
        entityManager.createQuery("UPDATE NodeDiscussion d SET d.parentDiscussion = null WHERE d.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        
        // Delete all dependent records via bulk JPQL to avoid FK constraints
        entityManager.createQuery("DELETE FROM NodeConnection c WHERE c.sourceNode.id = :nodeId OR c.targetNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM NodeProgress p WHERE p.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM NodeDiscussion d WHERE d.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM LessonSummary s WHERE s.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM SharedSolution s WHERE s.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM TestCase t WHERE t.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        
        // Delete Quiz related entities (vì Quiz không có @OneToMany cascade sang QuizQuestion)
        entityManager.createQuery("DELETE FROM QuizAttempt qa WHERE qa.quiz.id IN (SELECT q.id FROM Quiz q WHERE q.learningNode.id = :nodeId)").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM QuizQuestion qq WHERE qq.quiz.id IN (SELECT q.id FROM Quiz q WHERE q.learningNode.id = :nodeId)").setParameter("nodeId", nodeId).executeUpdate();
        
        // Delete GroupActivity related entities
        entityManager.createQuery("DELETE FROM StudyGroupMember sgm WHERE sgm.group.id IN (SELECT sg.id FROM StudyGroup sg WHERE sg.activity.learningNode.id = :nodeId)").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM StudyGroup sg WHERE sg.activity.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM GroupActivity g WHERE g.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();

        learningNodeRepository.delete(node);
    }

    @Override
    @Transactional
    public LearningNodeResponse updateLearningNode(Long nodeId, CreateLearningNodeRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());

        if (request.getContent() != null) {
            node.setContent(request.getContent());
        }
        if (request.getStarterCode() != null) {
            node.setStarterCode(request.getStarterCode());
        }
        if (request.getSolutionCode() != null) {
            node.setSolutionCode(request.getSolutionCode());
        }
        if (request.getNodeType() != null) {
            node.setNodeType(request.getNodeType());
        }
        if (request.getDisplayOrder() != null) {
            node.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getEstimatedMinutes() != null) {
            node.setEstimatedMinutes(request.getEstimatedMinutes());
        }

        LearningNode savedNode = learningNodeRepository.save(node);

        nodeConnectionRepository.deleteByTargetNodeId(savedNode.getId());
        if (request.getPrerequisiteNodeId() != null && request.getPrerequisiteNodeId() > 0) {
            LearningNode sourceNode = learningNodeRepository.findById(request.getPrerequisiteNodeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Prerequisite node"));
            NodeConnection connection = NodeConnection.builder()
                    .learningPath(savedNode.getLearningPath())
                    .sourceNode(sourceNode)
                    .targetNode(savedNode)
                    .conditionType("COMPLETION")
                    .build();
            nodeConnectionRepository.save(connection);
        }

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .description(savedNode.getDescription())
                .learningPathId(savedNode.getLearningPath() != null ? savedNode.getLearningPath().getId() : null)
                .learningSpaceId((savedNode.getLearningPath() != null && savedNode.getLearningPath().getLearningSpace() != null) ? savedNode.getLearningPath().getLearningSpace().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .isOptional(savedNode.getIsOptional())
                .content(savedNode.getContent())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

}
