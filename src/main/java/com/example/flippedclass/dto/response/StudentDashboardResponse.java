package com.example.flippedclass.dto.response;

import java.util.List;

public class StudentDashboardResponse {
    private DashboardUserResponse user;
    private StudentProfileResponse studentProfile;
    private List<DashboardLearningSpaceResponse> learningSpaces;
    private List<CourseDocumentResponse> recentDocuments;

    public StudentDashboardResponse(
            DashboardUserResponse user,
            StudentProfileResponse studentProfile,
            List<DashboardLearningSpaceResponse> learningSpaces,
            List<CourseDocumentResponse> recentDocuments
    ) {
        this.user = user;
        this.studentProfile = studentProfile;
        this.learningSpaces = learningSpaces;
        this.recentDocuments = recentDocuments;
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

    public List<CourseDocumentResponse> getRecentDocuments() {
        return recentDocuments;
    }
}
