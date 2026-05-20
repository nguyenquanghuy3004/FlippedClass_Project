package com.example.flippedclass.service;

import com.example.flippedclass.repository.LearningSpaceRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class InviteCodeGenerator {

    private final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int codeLength = 8;
    private final SecureRandom random = new SecureRandom();
    private final LearningSpaceRepository learningSpaceRepository;

    public InviteCodeGenerator(LearningSpaceRepository learningSpaceRepository) {
        this.learningSpaceRepository = learningSpaceRepository;
    }

    public String generateInviteCode() {
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String normalize(String inviteCode) {
        return inviteCode == null ? "" : inviteCode.trim().toUpperCase();
    }

    public String generateUniqueInviteCode() {
        String inviteCode;
        do {
            inviteCode = generateInviteCode();
        } while (learningSpaceRepository.existsByInviteCode(inviteCode));
        return inviteCode;
    }
}
