package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class DashboardLearningSpaceResponse {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String inviteCode;
    private String status;
    private String visibility;
    private String memberRole;
    private String memberStatus;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuizResponse> quizzes;

    public static DashboardLearningSpaceResponse from(LearningSpaceMember member, List<QuizResponse> quizzes) {
        LearningSpace space = member.getLearningSpace();
        DashboardLearningSpaceResponse response = new DashboardLearningSpaceResponse();
        response.id = space.getId();
        response.name = space.getName();
        response.description = space.getDescription();
        response.ownerId = space.getOwner() == null ? null : space.getOwner().getId();
        response.inviteCode = space.getInviteCode();
        response.status = space.getStatus() == null ? null : space.getStatus().name();
        response.visibility = space.getVisibility() == null ? null : space.getVisibility().name();
        response.memberRole = member.getRole() == null ? null : member.getRole().name();
        response.memberStatus = member.getStatus() == null ? null : member.getStatus().name();
        response.joinedAt = member.getJoinedAt();
        response.createdAt = space.getCreatedAt();
        response.updatedAt = space.getUpdatedAt();
        response.quizzes = quizzes;
        return response;
    }

    public static DashboardLearningSpaceResponse fromPublicSpace(LearningSpace space, List<QuizResponse> quizzes) {
        DashboardLearningSpaceResponse response = new DashboardLearningSpaceResponse();
        response.id = space.getId();
        response.name = space.getName();
        response.description = space.getDescription();
        response.ownerId = space.getOwner() == null ? null : space.getOwner().getId();
        response.inviteCode = space.getInviteCode();
        response.status = space.getStatus() == null ? null : space.getStatus().name();
        response.visibility = space.getVisibility() == null ? null : space.getVisibility().name();
        response.memberRole = "MEMBER";
        response.memberStatus = "ACTIVE";
        response.joinedAt = space.getCreatedAt();
        response.createdAt = space.getCreatedAt();
        response.updatedAt = space.getUpdatedAt();
        response.quizzes = quizzes;
        return response;
    }
}
