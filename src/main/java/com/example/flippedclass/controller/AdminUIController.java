package com.example.flippedclass.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import com.example.flippedclass.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUIController {

    private final AdminDashboardService dashboardService;
    @GetMapping
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("stats", dashboardService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String usersPage() {
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String userDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user-detail";
    }

    @GetMapping("/spaces")
    public String spacesPage() {
        return "admin/spaces";
    }

    @GetMapping("/spaces/{id}")
    public String spaceDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("spaceId", id);
        return "admin/space-detail";
    }

    @GetMapping("/paths")
    public String pathsPage() {
        return "admin/paths";
    }

    @GetMapping("/nodes")
    public String nodesPage() {
        return "admin/nodes";
    }

}
