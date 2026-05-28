package com.example.flippedclass.dto;

import java.util.List;

public class StudentDashboardResponse {
    private DashboardUserResponse user;
    private StudentProfileResponse studentProfile;
    private List<DashboardLearningSpaceResponse> learningSpaces;

    public StudentDashboardResponse(
            DashboardUserResponse user,
            StudentProfileResponse studentProfile,
            List<DashboardLearningSpaceResponse> learningSpaces
    ) {
        this.user = user;
        this.studentProfile = studentProfile;
        this.learningSpaces = learningSpaces;
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
}
