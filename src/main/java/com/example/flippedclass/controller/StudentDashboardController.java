package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// Quản lý API dashboard của sinh viên
@RestController
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    // Lấy dữ liệu dashboard cho sinh viên
    @GetMapping("/api/students/{studentId}/dashboard")
    public StudentDashboardResponse getDashboard(@PathVariable Long studentId) {
        return studentDashboardService.getDashboard(studentId);
    }
}
