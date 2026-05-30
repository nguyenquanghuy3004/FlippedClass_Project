package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.StudentDashboardResponse;

// Xử lý dữ liệu dashboard cho sinh viên
public interface StudentDashboardService {

    public StudentDashboardResponse getDashboard(Long studentId);
}
