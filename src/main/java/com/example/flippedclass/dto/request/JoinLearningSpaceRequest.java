package com.example.flippedclass.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinLearningSpaceRequest {
    private Long spaceId;
    private String inviteCode;
}