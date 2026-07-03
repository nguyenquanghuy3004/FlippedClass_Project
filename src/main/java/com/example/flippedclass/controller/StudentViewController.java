package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.StudentDashboardResponse;
import com.example.flippedclass.service.StudentDashboardService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StudentViewController {

    private final StudentDashboardService dashboardService;

    @GetMapping("/student/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetailsImpl user) {
        if (user != null) {
            StudentDashboardResponse dashboard = dashboardService.getDashboard(user.getId());
            model.addAttribute("dashboard", dashboard);
        }
        return "student/student-dashboard";
    }
}
