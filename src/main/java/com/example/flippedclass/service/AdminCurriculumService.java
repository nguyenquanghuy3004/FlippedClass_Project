package com.example.flippedclass.service;

import com.example.flippedclass.dto.AdminLearningNodeDto;
import com.example.flippedclass.dto.AdminLearningPathDto;
import com.example.flippedclass.enums.LearningPathStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCurriculumService {
    Page<AdminLearningPathDto> getAllPaths(String keyword, LearningPathStatus status, Pageable pageable);
    void updatePathStatus(Long pathId, LearningPathStatus status);
    
    Page<AdminLearningNodeDto> getAllNodes(String keyword, String status, Pageable pageable);
    void updateNodeStatus(Long nodeId, String status);
}
