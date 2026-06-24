package com.example.flippedclass.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalSpaces;
    private long totalPaths;
    private long totalNodes;
}
