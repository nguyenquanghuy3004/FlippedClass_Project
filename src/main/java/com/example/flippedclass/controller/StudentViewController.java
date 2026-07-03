package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.CourseDocumentResponse;
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
            
            java.util.List<com.example.flippedclass.dto.response.DashboardLearningSpaceResponse> joinedSpaces = new java.util.ArrayList<>();
            if (dashboard.getLearningSpaces() != null) {
                joinedSpaces = dashboard.getLearningSpaces().stream()
                    .filter(space -> !"ARCHIVED".equals(space.getStatus()) && (space.isJoined() || "STUDENT".equals(space.getMemberRole())))
                    .collect(java.util.stream.Collectors.toList());
            }
            model.addAttribute("joinedSpaces", joinedSpaces);
            
            // Filter recent documents to remove videos and youtube links, matching frontend logic, and limit to 8
            java.util.List<CourseDocumentResponse> recentDocs = dashboard.getRecentDocuments();
            if (recentDocs != null) {
                java.util.List<CourseDocumentResponse> filteredDocs = recentDocs.stream()
                    .filter(doc -> {
                        String type = doc.getDocumentType() == null ? "OTHER" : doc.getDocumentType().name();
                        String url = (doc.getUrl() != null ? doc.getUrl() : "").toLowerCase();
                        if ("VIDEO".equals(type) || "YOUTUBE".equals(type)) return false;
                        if (url.matches(".*\\.(mp4|webm|mov|avi|mkv)(?:$|[?#]).*")) return false;
                        return true;
                    })
                    .limit(8)
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("recentDocuments", filteredDocs);
            }
        }
        return "student/student-dashboard";
    }
}
