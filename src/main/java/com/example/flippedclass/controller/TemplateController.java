package com.example.flippedclass.controller;

import com.example.flippedclass.service.LearningPathService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TemplateController {

    @GetMapping("/")
    public String index() {
        return "homePage";
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
        return "Authen/signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "Authen/signup";
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

    @GetMapping("/lecturer/mentoring")
    public String peerMentoring() {
        return "lecturer/mentoring";
    }

    @GetMapping("/supporter/dashboard")
    public String supporterDashboard() {
        return "supporter/dashboard";
    }

    @GetMapping("/lecturer/quizzes/{id}/statistics")
    public String quizStatistics() {
        return "lecturer/quiz-statistics";
    }

    @GetMapping("/supporter/quizzes/{id}/builder")
    public String supporterQuizBuilder() {
        return "supporter/quiz-builder";
    }

    @GetMapping("/supporter/quizzes/{id}/statistics")
    public String supporterQuizStatistics() {
        return "supporter/quiz-statistics";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student/student-dashboard";
    }

    @GetMapping("/student/learning-spaces")
    public String studentLearningSpaces() {
        return "student/learning-spaces";
    }

    @GetMapping({"/student/learning-node", "/student/learning-nodes/{nodeId}"})
    public String studentLearningNode() {
        return "student/learning-node";
    }

    @GetMapping("/student/my-quizzes")
    public String studentMyQuizzes() {
        return "student/my-quizzes";
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

    @GetMapping("/lecturer/evaluation-sessions/{sessionId}/students")
    public String evaluationStudents(@PathVariable Long sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "lecturer/evaluation-students";
    }

    @GetMapping("/lecturer/dashboard")
    public String lecturerDashboard() {
        return "lecturer/dashboard";
    }

    @Autowired
    private LearningPathService learningPathService;

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

    @GetMapping("/supporter/space/{spaceId}")
    public String supporterLearningPath(@PathVariable Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        model.addAttribute("isSupporter", true);
        try {
            model.addAttribute("paths", learningPathService.getLearningPath(spaceId, null));
        } catch(Exception e) {
            model.addAttribute("paths", java.util.Collections.emptyList());
        }
        return "supporter/learningPath";
    }

    @GetMapping("/lecturer/learning-nodes/{nodeId}/preview")
    public String lecturerNodePreview(@PathVariable Long nodeId, Model model) {
        model.addAttribute("nodeId", nodeId);
        return "lecturer/node-preview";
    }

    @GetMapping("/lecturer/space-members")
    public String spaceMembers(@RequestParam("spaceId") Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        return "lecturer/space-members";
    }

    @GetMapping("/supporter/space-members")
    public String supporterSpaceMembers(@RequestParam("spaceId") Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        return "supporter/space-members";
    }


    @GetMapping("/lecturer/spaces/{spaceId}/analytics")
    public String learningAnalytics(@PathVariable Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        return "lecturer/learning-analytics";
    }

    @GetMapping("/lecturer/spaces/{spaceId}/group-activities")
    public String lecturerGroupActivities(@PathVariable Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        return "lecturer/group-activities";
    }

    @GetMapping("/lecturer/group-activities/{activityId}")
    public String lecturerActivityDetail(@PathVariable Long activityId, Model model) {
        model.addAttribute("activityId", activityId);
        return "lecturer/activity-detail";
    }

    @GetMapping("/lecturer/groups/{groupId}/review")
    public String lecturerGroupReview(@PathVariable Long groupId, Model model) {
        model.addAttribute("groupId", groupId);
        return "lecturer/group-review";
    }

    @GetMapping("/lecturer/summaries/{summaryId}/review")
    public String summaryReview(@PathVariable Long summaryId, Model model) {
        model.addAttribute("summaryId", summaryId);
        return "lecturer/summary-review";
    }
}
