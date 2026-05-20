package com.example.flippedclass.util;

import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;

public class InviteCodeGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LEGHT = 8;

    private final SecureRandom random = new SecureRandom();

   @Autowired
    LearningSpaceRepository learningSpaceRepository;

   // generate random invite code
    public String generateInviteCode(){
        StringBuilder sb = new StringBuilder(CODE_LEGHT);
        for (int i = 0; i < CODE_LEGHT ; i++){
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    // chuẩn hóa mã User nhập khi join
    public String normallize(String inviteCode){
        return inviteCode == null ? "" : inviteCode.trim().toLowerCase();
    }

    public String generateUniqueInviteCode() {
        String inviteCode;
        do {
            inviteCode = generateInviteCode();
        } while (learningSpaceRepository.existsByInviteCode(inviteCode));
        return inviteCode;

    }
}