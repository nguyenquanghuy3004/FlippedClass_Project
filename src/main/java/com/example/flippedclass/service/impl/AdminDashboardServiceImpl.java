package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.AdminDashboardStatsDto;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningPathRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final LearningSpaceRepository spaceRepository;
    private final LearningPathRepository pathRepository;
    private final LearningNodeRepository nodeRepository;

    @Override
    public AdminDashboardStatsDto getDashboardStats() {
        return AdminDashboardStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalSpaces(spaceRepository.count())
                .totalPaths(pathRepository.count())
                .totalNodes(nodeRepository.count())
                .build();
    }
}
