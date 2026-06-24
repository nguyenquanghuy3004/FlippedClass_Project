package com.example.flippedclass.dto.response;

import java.util.List;

public class StudentDashboardResponse {
    private DashboardUserResponse user;
    private StudentProfileResponse studentProfile;
    private List<DashboardLearningSpaceResponse> learningSpaces;
    private List<RecentDocumentResponse> recentDocuments;
    private List<CommentNotificationResponse> commentNotifications;

    public StudentDashboardResponse(
            DashboardUserResponse user,
            StudentProfileResponse studentProfile,
            List<DashboardLearningSpaceResponse> learningSpaces,
            List<RecentDocumentResponse> recentDocuments,
            List<CommentNotificationResponse> commentNotifications
    ) {
        this.user = user;
        this.studentProfile = studentProfile;
        this.learningSpaces = learningSpaces;
        this.recentDocuments = recentDocuments;
        this.commentNotifications = commentNotifications;
    }

    public DashboardUserResponse getUser() {
        return user;
    }

    public StudentProfileResponse getStudentProfile() {
        return studentProfile;
    }

    public List<DashboardLearningSpaceResponse> getLearningSpaces() {
        return learningSpaces;
    }

    public List<RecentDocumentResponse> getRecentDocuments() {
        return recentDocuments;
    }

    public List<CommentNotificationResponse> getCommentNotifications() {
        return commentNotifications;
    }
}
