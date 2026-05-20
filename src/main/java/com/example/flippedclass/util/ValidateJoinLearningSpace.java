package com.example.flippedclass.util;

import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidateJoinLearningSpace {
    public void validate(JoinLearningSpaceRequest request){
        if(request == null || request.getInviteCode().trim().isEmpty() || request.getInviteCode() == null){
            throw new IllegalArgumentException("Mã mời không được để trống");
        }
    }
}
