package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.res.LearningSpaceResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LearningSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LearningSpaceServiceImpl implements LearningSpaceService {

    @Autowired
    private LearningSpaceRepository learningSpaceRepository;

    @Autowired
    private LearningSpaceMemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request) {
        // Manual validation for name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên Learning Space không được để trống");
        }

        // 1. Get current logged in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));

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
// Delete learning Space
    @Override
    public void deleteLearningSpace(Long id){

        // 1. Lấy thông tin user đang đăng nhập
        Object mentor = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (mentor instanceof UserDetails) ? ((UserDetails) mentor).getUsername() : mentor.toString();


        LearningSpace learningSpace = learningSpaceRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learing space or đã bị xóa"));

        if(!learningSpace.getOwner().getUsername().equals(username)){
            throw new IllegalArgumentException("Bạn khoongg có quyền xóa");

        }
        learningSpace.setDeleted(true);
        learningSpaceRepository.save(learningSpace);
    }

    // Restore learning Space
    @Override
    public void restoreLearningSpace(Long id){

        // 1. Lấy thông tin user đang đăng nhập
        Object mentor = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (mentor instanceof UserDetails) ? ((UserDetails) mentor).getUsername() : mentor.toString();

        // Tim lowp
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy learning space"));

        // Kieernm tra xem ai là ng có thê restore
        if(!learningSpace.getOwner().getUsername().equals(username)){
            throw new IllegalArgumentException("Bạn không có quyền khôi phục learning space");
        }

        //Ktra lớp có thực sự bị xóa
        if(!learningSpace.isDeleted()){
            throw new IllegalArgumentException("Lớp đang hoạt động, không cần khôi phục !!!");
        }

        // Khôi phụcc
        learningSpace.setDeleted(false);
        learningSpaceRepository.save(learningSpace);
    }
}
