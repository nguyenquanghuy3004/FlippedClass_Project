package com.example.flippedclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class TemplateController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory";
    }

    @GetMapping("/create-product")
    public String createProduct() {
        return "create-product";
    }

    @GetMapping("/reports")
    public String reports() {
        return "reports";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/docs")
    public String docs() {
        return "docs";
    }

    @GetMapping("/404-error")
    public String error404() {
        return "404-error";
    }

    @GetMapping("/lecturer/quizzes")
    public String quizzes() {
        return "lecturer/quizzes";
    }

    @GetMapping("/lecturer/quizzes/{id}/builder")
    public String quizBuilder() {
        return "lecturer/quiz-builder";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student/student-dashboard";
    }

    @GetMapping("/student/take-quiz")
    public String takeQuiz() {
        return "student/take-quiz";
    }

    @GetMapping("/lecturer/grading")
    public String grading() {
        return "lecturer/grading";
    }

    @GetMapping("/lecturer/evaluation-sessions")
    public String evaluationSessions() {
        return "lecturer/evaluation-sessions";
    }

    @GetMapping("/mentor/dashboard")
    public String mentorDashboard() {
        return "lecturer/dashboard";
    }

    @Autowired
    private com.example.flippedclass.service.LearningPathService learningPathService;

    @GetMapping("/lecturer/space/{spaceId}")
    public String learningPath(@PathVariable Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        try {
            model.addAttribute("paths", learningPathService.getLearningPath(spaceId));
        } catch(Exception e) {
            model.addAttribute("paths", java.util.Collections.emptyList());
        }
        return "lecturer/learningPath";
    }
}
