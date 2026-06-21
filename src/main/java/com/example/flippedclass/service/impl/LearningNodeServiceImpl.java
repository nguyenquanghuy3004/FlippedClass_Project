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

    @Autowired
    private com.example.flippedclass.repository.NodeConnectionRepository nodeConnectionRepository;
    
    @Autowired
    private com.example.flippedclass.repository.LearningSpaceRepository learningSpaceRepository;

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

        Long prereqId = null;
        if (request.getPrerequisiteNodeId() != null) {
            com.example.flippedclass.entity.NodeConnection conn = new com.example.flippedclass.entity.NodeConnection();
            conn.setLearningPath(learningPath);
            conn.setTargetNode(savedNode);
            conn.setSourceNode(learningNodeRepository.findById(request.getPrerequisiteNodeId()).orElseThrow());
            conn.setConditionType("COMPLETED");
            nodeConnectionRepository.save(conn);
            prereqId = request.getPrerequisiteNodeId();
        }

        return buildResponse(savedNode, prereqId);
    }

    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        if(!learningNodeRepository.existsById(nodeId)){
            throw new IllegalArgumentException("Không tìm thấy bài học");
        }
        nodeConnectionRepository.deleteByTargetNodeId(nodeId);
        learningNodeRepository.deleteById(nodeId);
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

        LearningNode savedNode = learningNodeRepository.save(node);

        nodeConnectionRepository.deleteByTargetNodeId(nodeId);
        Long prereqId = null;
        if (request.getPrerequisiteNodeId() != null) {
            com.example.flippedclass.entity.NodeConnection conn = new com.example.flippedclass.entity.NodeConnection();
            conn.setLearningPath(node.getLearningPath());
            conn.setTargetNode(savedNode);
            conn.setSourceNode(learningNodeRepository.findById(request.getPrerequisiteNodeId()).orElseThrow());
            conn.setConditionType("COMPLETED");
            nodeConnectionRepository.save(conn);
            prereqId = request.getPrerequisiteNodeId();
        }

        return buildResponse(savedNode, prereqId);
    }

    private LearningNodeResponse buildResponse(LearningNode savedNode, Long prereqId) {
        return LearningNodeResponse.builder()
                .id(savedNode.getId())
                .title(savedNode.getTitle())
                .description(savedNode.getDescription())
                .learningPathId(savedNode.getLearningPath() != null ? savedNode.getLearningPath().getId() : null)
                .learningSpaceId((savedNode.getLearningPath() != null && savedNode.getLearningPath().getLearningSpace() != null) ? savedNode.getLearningPath().getLearningSpace().getId() : null)
                .status(savedNode.getStatus())
                .nodeType(savedNode.getNodeType())
                .content(savedNode.getContent())
                .starterCode(savedNode.getStarterCode())
                .solutionCode(savedNode.getSolutionCode())
                .prerequisiteNodeId(prereqId)
                .createdAt(savedNode.getCreatedAt())
                .updatedAt(savedNode.getUpdatedAt())
                .build();
    }

}
