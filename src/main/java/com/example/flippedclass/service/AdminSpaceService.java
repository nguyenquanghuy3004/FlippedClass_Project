package com.example.flippedclass.service;

import com.example.flippedclass.dto.LearningSpaceDetailDto;
import com.example.flippedclass.dto.LearningSpaceDto;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.enums.LearningSpaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AdminSpaceService {
    Page<LearningSpaceDto> getAllPages(String keyword, LearningSpaceStatus status, Pageable pageable);
    LearningSpaceDetailDto getSpaceDetail(Long spaceId);
    void updateSpaceStatus(Long spaceId, LearningSpaceStatus newStatus);
}

