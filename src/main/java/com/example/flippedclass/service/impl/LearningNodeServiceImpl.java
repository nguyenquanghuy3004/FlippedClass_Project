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
                // .positionX(request.getPositionX())
                // .positionY(request.getPositionY())
                .status("ACTIVE")
//                .nodeType("VIDEO") // Mặc định hoặc lấy từ request nếu có
                .nodeType(request.getNoteType() != null ? request.getNoteType() : "VIDEO")
                .build();

        LearningNode savedNode = learningNodeRepository.save(node);

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
//                .description(savedNode.getDescription())
                .learningPathId(learningPath.getId())
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        if(!learningNodeRepository.existsById(nodeId)){
            throw new IllegalArgumentException("Không tìm thấy bài học");
        }
        learningNodeRepository.deleteById(nodeId);
    }

    @Override
    @Transactional
    public LearningNodeResponse updateLearningNode(Long nodeId, CreateLearningNodeRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());
        if (request.getNoteType() != null) {
            node.setNodeType(request.getNoteType());
        }

        LearningNode savedNode = learningNodeRepository.save(node);

        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .learningPathId(savedNode.getLearningPath() != null ? savedNode.getLearningPath().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

}
