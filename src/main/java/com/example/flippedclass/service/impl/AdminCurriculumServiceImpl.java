package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.AdminLearningNodeDto;
import com.example.flippedclass.dto.AdminLearningPathDto;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.service.AdminCurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCurriculumServiceImpl implements AdminCurriculumService {

    private final LearningPathRepository pathRepository;
    private final LearningNodeRepository nodeRepository;

    @Override
    public Page<AdminLearningPathDto> getAllPaths(String keyword, LearningPathStatus status, Pageable pageable) {
        Page<LearningPath> paths = pathRepository.findAllForAdmin(keyword, status, pageable);
        return paths.map(this::mapToPathDto);
    }

    @Override
    @Transactional
    public void updatePathStatus(Long pathId, LearningPathStatus status) {
        LearningPath path = pathRepository.findById(pathId).orElseThrow(() -> new RuntimeException("Path not found"));
        path.setStatus(status);
        pathRepository.save(path);
    }

    @Override
    public Page<AdminLearningNodeDto> getAllNodes(String keyword, String status, Pageable pageable) {
        Page<LearningNode> nodes = nodeRepository.findAllForAdmin(keyword, status, pageable);
        return nodes.map(this::mapToNodeDto);
    }

    @Override
    @Transactional
    public void updateNodeStatus(Long nodeId, String status) {
        LearningNode node = nodeRepository.findById(nodeId).orElseThrow(() -> new RuntimeException("Node not found"));
        node.setStatus(status);
        nodeRepository.save(node);
    }

    private AdminLearningPathDto mapToPathDto(LearningPath p) {
        return AdminLearningPathDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .spaceId(p.getLearningSpace().getId())
                .spaceName(p.getLearningSpace().getName())
                .lecturerId(p.getLecturer().getId())
                .lecturerEmail(p.getLecturer().getEmail())
                .status(p.getStatus())
                .visibility(p.getVisibility())
                .nodeCount(p.getNodes().size())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private AdminLearningNodeDto mapToNodeDto(LearningNode n) {
        return AdminLearningNodeDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .description(n.getDescription())
                .nodeType(n.getNodeType())
                .status(n.getStatus())
                .pathId(n.getLearningPath().getId())
                .pathTitle(n.getLearningPath().getTitle())
                .spaceName(n.getLearningPath().getLearningSpace().getName())
                .itemCount(n.getItems().size())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
