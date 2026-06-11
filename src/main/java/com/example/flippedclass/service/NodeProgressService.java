package com.example.flippedclass.service;

import com.example.flippedclass.entity.NodeProgress;
import com.example.flippedclass.dto.StudentProgressDTO;

public interface NodeProgressService {
    NodeProgress getOrCreateProgress(Long studentId, Long nodeId);

    NodeProgress updateToInProgress(Long studentId, Long nodeId);

    NodeProgress updateToComplete(Long studentId, Long nodeId);

    NodeProgress getProgress(Long studentId, Long nodeId);

    StudentProgressDTO calculateProgress(Long studentId, Long spaceId);

}
