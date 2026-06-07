package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/api/students/{studentId}/dashboard")
    public StudentDashboardResponse getDashboard(@PathVariable Long studentId) {
        return studentDashboardService.getDashboard(studentId);
    }
}
