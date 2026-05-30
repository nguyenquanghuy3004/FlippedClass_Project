package com.example.flippedclass.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentDashboardResponse {
    private DashboardUserResponse user;
    private StudentProfileResponse studentProfile;
    private List<DashboardLearningSpaceResponse> learningSpaces;
}
