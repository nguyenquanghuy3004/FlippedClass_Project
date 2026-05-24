package com.example.flippedclass.dto.res;

import enums.MemberRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinLearningSpaceResponse {
    private String message;
    private Long learningSpaceId;
    private String learningSpaceName;
    private MemberRole role;
}
