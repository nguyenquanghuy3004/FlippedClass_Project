package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.UpdateStudentProfileRequest;
import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @com.example.flippedclass.annotation.LogUserActivity(actionType = "VIEW_DASHBOARD", description = "'Đã truy cập Dashboard cá nhân'")
    @GetMapping("/api/students/{studentId}/dashboard")
    public StudentDashboardResponse getDashboard(@PathVariable Long studentId) {
        return studentDashboardService.getDashboard(studentId);
    }

    @com.example.flippedclass.annotation.LogUserActivity(actionType = "UPDATE_PROFILE", description = "'Đã cập nhật thông tin cá nhân'")
    @PutMapping("/api/students/{studentId}/profile")
    public StudentDashboardResponse updateProfile(
            @PathVariable Long studentId,
            @RequestBody UpdateStudentProfileRequest request
    ) {
        return studentDashboardService.updateProfile(studentId, request);
    }
}
