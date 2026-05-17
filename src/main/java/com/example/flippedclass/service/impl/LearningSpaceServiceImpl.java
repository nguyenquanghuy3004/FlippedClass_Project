package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.LearningSpaceResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LearningSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LearningSpaceServiceImpl implements LearningSpaceService {

    @Autowired
    private LearningSpaceRepository learningSpaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request) {
        // 1. Get current logged in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Generate unique invite code
        String inviteCode;
        do {
            inviteCode = generateRandomString(8);
        } while (learningSpaceRepository.existsByInviteCode(inviteCode));

        // 3. Create Entity and save
        LearningSpace learningSpace = new LearningSpace();
        learningSpace.setName(request.getName());
        learningSpace.setDescription(request.getDescription());
        learningSpace.setVisibility(request.getVisibility());
        learningSpace.setOwner(owner);
        learningSpace.setInviteCode(inviteCode);
        
        LearningSpace savedSpace = learningSpaceRepository.save(learningSpace);

        // 4. Return Response DTO
        return LearningSpaceResponse.builder()
                .id(savedSpace.getId())
                .name(savedSpace.getName())
                .description(savedSpace.getDescription())
                .inviteCode(savedSpace.getInviteCode())
                .visibility(savedSpace.getVisibility())
                .ownerId(savedSpace.getOwner().getId())
                .ownerUsername(savedSpace.getOwner().getUsername())
                .createdAt(savedSpace.getCreatedAt())
                .build();
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
