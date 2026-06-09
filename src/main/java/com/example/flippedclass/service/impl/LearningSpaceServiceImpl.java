package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.response.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.response.LearningSpaceResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LearningSpaceService;
import com.example.flippedclass.service.InviteCodeGenerator;
import com.example.flippedclass.util.ValidateJoinLearningSpace;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.enums.MemberStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningSpaceServiceImpl implements LearningSpaceService {

    @Autowired
    private LearningSpaceRepository learningSpaceRepository;

    @Autowired
    private LearningSpaceMemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;

    @Autowired
    private ValidateJoinLearningSpace validateJoinLearningSpace;


    private User getCurrentUser() {
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

    // Kiểm tra user hiện tại có phải ADMIN không — ADMIN bypass mọi kiểm tra owner
    private boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }


    @Transactional
    @Override
    public LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request) {

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên Learning Space không được để trống");
        }

        User owner = getCurrentUser();

        // Create Entity and save
        String inviteCode = inviteCodeGenerator.generateUniqueInviteCode();

        LearningSpace learningSpace = new LearningSpace();
        learningSpace.setName(request.getName());
        learningSpace.setDescription(request.getDescription());
        learningSpace.setVisibility(request.getVisibility());
        learningSpace.setOwner(owner);
        learningSpace.setInviteCode(inviteCode);
        
        LearningSpace savedSpace = learningSpaceRepository.save(learningSpace);

        // tạo luôn OWNER trong bảng member (không bắt buộc spec join, nhưng nên có)
        LearningSpaceMember ownerMember = new LearningSpaceMember();
        ownerMember.setLearningSpace(savedSpace);
        ownerMember.setUser(owner);
        ownerMember.setRole(MemberRole.OWNER);
        ownerMember.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(ownerMember);

        // Return Response DTO
        return LearningSpaceResponse.builder()
                .id(savedSpace.getId())
                .name(savedSpace.getName())
                .description(savedSpace.getDescription())
                .inviteCode(savedSpace.getInviteCode())
                .visibility(savedSpace.getVisibility())
                .ownerId(savedSpace.getOwner().getId())
                .ownerUsername(savedSpace.getOwner().getUsername())
                .createdAt(savedSpace.getCreatedAt())
                .status(savedSpace.getStatus())
                .build();
    }

    @Override
    public List<LearningSpaceResponse> getMySpaces() {
        User currentUser = getCurrentUser();
        List<LearningSpace> spaces = learningSpaceRepository.findByOwnerId(currentUser.getId());
        
        return spaces.stream().map(space -> LearningSpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .inviteCode(space.getInviteCode())
                .visibility(space.getVisibility())
                .ownerId(space.getOwner().getId())
                .ownerUsername(space.getOwner().getUsername())
                .createdAt(space.getCreatedAt())
                .status(space.getStatus())
                .build()).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public LearningSpace updateLearningSpace(Long id, LearningSpace spaceDetail){
        LearningSpace space = learningSpaceRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Learning Space hoặc đã bị xóa"));

        // ADMIN bypass kiểm tra owner
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!space.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("Bạn không có quyền cập nhật Learning Space này");
            }
        }

        space.setName(spaceDetail.getName());
        space.setDescription(spaceDetail.getDescription());
        space.setVisibility(spaceDetail.getVisibility());
        return learningSpaceRepository.save(space);
    }




    @Override
    @Transactional
    public JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request) {
        // Validate request
        validateJoinLearningSpace.validate(request);

        LearningSpace learningSpace = learningSpaceRepository.findByInviteCodeAndStatus(request.getInviteCode(), LearningSpaceStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Could not find a class with this invite code or the class has been deleted"));


        User currentUser = getCurrentUser();

        if (memberRepository.existsByLearningSpaceAndUser(learningSpace, currentUser)) {
            throw new IllegalArgumentException("Bạn đã tham gia lớp học này rồi");
        }

        LearningSpaceMember member = new LearningSpaceMember();
        member.setLearningSpace(learningSpace);
        member.setUser(currentUser);
        member.setRole(MemberRole.MEMBER);
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        // Return Response
        JoinLearningSpaceResponse response = new JoinLearningSpaceResponse();
        response.setMessage("Tham gia lớp học thành công!");
        response.setLearningSpaceId(learningSpace.getId());
        response.setLearningSpaceName(learningSpace.getName());
        response.setRole(MemberRole.MEMBER);
        return response;
    }

    @Override
    @Transactional
    public void deleteLearningSpace(Long id) {
        LearningSpace learningSpace = learningSpaceRepository
                .findByIdAndStatus(id, LearningSpaceStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Space hoặc đã bị xóa"));

        // ADMIN bypass kiểm tra owner
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("Bạn không có quyền xóa");
            }
        }

        learningSpace.setStatus(LearningSpaceStatus.DELETE);
        learningSpaceRepository.save(learningSpace);
    }



    @Override
    @Transactional
    public void restoreLearningSpace(Long id) {
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy learning space"));

        // ADMIN bypass kiểm tra owner
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("Bạn không có quyền khôi phục learning space");
            }
        }

        if (learningSpace.getStatus() == LearningSpaceStatus.ACTIVE) {
            throw new IllegalArgumentException("Lớp đang hoạt động, không cần khôi phục");
        }

        learningSpace.setStatus(LearningSpaceStatus.ACTIVE);
        learningSpaceRepository.save(learningSpace);
    }

    @Override
    @Transactional
    public void archiveLearningSpace(Long id) {
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy learning space"));

        // ADMIN bypass kiểm tra owner
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("Bạn không có quyền lưu trữ learning space");
            }
        }

        if (learningSpace.getStatus() == LearningSpaceStatus.ARCHIVE) {
            throw new IllegalArgumentException("Lớp đã được lưu trữ");
        }

        learningSpace.setStatus(LearningSpaceStatus.ARCHIVE);
        learningSpaceRepository.save(learningSpace);
    }


}
