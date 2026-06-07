package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.VisibilityType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LearningSpaceResponse {
    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private VisibilityType visibility;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LearningSpaceStatus status;
}
