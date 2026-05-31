package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.StudentDashboardResponse;

public interface StudentDashboardService {

    public StudentDashboardResponse getDashboard(Long studentId);
}
