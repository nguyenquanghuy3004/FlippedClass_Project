package com.example.flippedclass.dto.response;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.VisibilityType;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.enums.MemberStatus;
import java.time.LocalDateTime;

public class DashboardLearningSpaceResponse {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String inviteCode;
    private LearningSpaceStatus status;
    private VisibilityType visibility;
    private MemberRole memberRole;
    private MemberStatus memberStatus;
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
        response.status = space.getStatus();
        response.visibility = space.getVisibility();
        response.memberRole = member.getRole();
        response.memberStatus = member.getStatus();
        response.joinedAt = member.getJoinedAt();
        response.createdAt = space.getCreatedAt();
        response.updatedAt = space.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public LearningSpaceStatus getStatus() {
        return status;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
