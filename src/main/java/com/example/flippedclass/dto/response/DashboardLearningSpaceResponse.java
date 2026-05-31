package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import java.time.LocalDateTime;
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

    public static DashboardLearningSpaceResponse from(LearningSpaceMember member) {
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
        return response;
    }
}
