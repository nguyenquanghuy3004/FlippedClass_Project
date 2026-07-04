package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.repository.LearningNodeItemRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningPathRepository;
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

    @Override
    public LearningNodeResponse createLearningNode(Long pathId, CreateLearningNodeRequest request) {
        LearningPath learningPath = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Learning Path"));

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
    private LearningNodeItemRepository learningNodeItemRepository;

    @Autowired
    private QuizRepository quizRepository;

    @jakarta.persistence.PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài học"));
        
        // Break self-referencing relationships first
        entityManager.createQuery("UPDATE NodeDiscussion d SET d.parentDiscussion = null WHERE d.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("UPDATE NodeComment c SET c.parentComment = null WHERE c.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        
        // Delete all dependent records via bulk JPQL to avoid FK constraints
        entityManager.createQuery("DELETE FROM NodeConnection c WHERE c.sourceNode.id = :nodeId OR c.targetNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM NodeProgress p WHERE p.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM NodeDiscussion d WHERE d.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM NodeComment c WHERE c.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM LessonSummary s WHERE s.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM SharedSolution s WHERE s.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM TestCase t WHERE t.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();
        entityManager.createQuery("DELETE FROM GroupActivity g WHERE g.learningNode.id = :nodeId").setParameter("nodeId", nodeId).executeUpdate();

        // Items and quizzes are mapped with CascadeType.ALL in LearningNode, 
        // so Hibernate will automatically delete them when we delete the node.
        // Do NOT use deleteAllInBatch here as it causes StaleStateException.
        
        learningNodeRepository.delete(node);
    }

    @Override
    @Transactional
    public LearningNodeResponse updateLearningNode(Long nodeId, CreateLearningNodeRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

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
