package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.res.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.res.LearningSpaceResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.InviteCodeGenerator;
import com.example.flippedclass.service.LearningSpaceService;
import com.example.flippedclass.util.ValidateJoinLearningSpace;
import enums.LearningSpaceStatus;
import enums.MemberRole;
import enums.MemberStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LearningSpaceServiceImpl implements LearningSpaceService {

    private final LearningSpaceRepository learningSpaceRepository;
    private final LearningSpaceMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final ValidateJoinLearningSpace validateJoinLearningSpace;

    public LearningSpaceServiceImpl(
            LearningSpaceRepository learningSpaceRepository,
            LearningSpaceMemberRepository memberRepository,
            UserRepository userRepository,
            InviteCodeGenerator inviteCodeGenerator,
            ValidateJoinLearningSpace validateJoinLearningSpace) {
        this.learningSpaceRepository = learningSpaceRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.inviteCodeGenerator = inviteCodeGenerator;
        this.validateJoinLearningSpace = validateJoinLearningSpace;
    }

    @Override
    @Transactional
    public LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên Learning Space không được để trống");
        }

        User owner = getCurrentUser();
        String inviteCode = inviteCodeGenerator.generateUniqueInviteCode();

        LearningSpace learningSpace = new LearningSpace();
        learningSpace.setName(request.getName().trim());
        learningSpace.setDescription(request.getDescription());
        learningSpace.setVisibility(request.getVisibility());
        learningSpace.setOwner(owner);
        learningSpace.setInviteCode(inviteCode);
        learningSpace.setStatus(LearningSpaceStatus.ACTIVE);

        LearningSpace savedSpace = learningSpaceRepository.save(learningSpace);

        LearningSpaceMember ownerMember = new LearningSpaceMember();
        ownerMember.setLearningSpace(savedSpace);
        ownerMember.setUser(owner);
        ownerMember.setRole(MemberRole.OWNER);
        ownerMember.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(ownerMember);

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

    @Override
    @Transactional
    public JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request) {
        validateJoinLearningSpace.validate(request);

        User currentUser = getCurrentUser();
        String normalizedCode = inviteCodeGenerator.normalize(request.getInviteCode());

        LearningSpace space = learningSpaceRepository.findByInviteCode(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Mã mời không hợp lệ"));

        if (space.getStatus() == LearningSpaceStatus.ARCHIVE) {
            throw new IllegalArgumentException("Lớp đã được lưu trữ, không thể tham gia");
        }
        if (space.getStatus() == LearningSpaceStatus.DELETE) {
            throw new IllegalArgumentException("Lớp đã bị xóa, không thể tham gia");
        }
        if (space.getStatus() != LearningSpaceStatus.ACTIVE) {
            throw new IllegalArgumentException("Lớp không khả dụng để tham gia");
        }

        if (space.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn là chủ sở hữu lớp này, không cần tham gia bằng mã mời");
        }

        if (memberRepository.existsByLearningSpaceAndUser(space, currentUser)) {
            throw new IllegalArgumentException("Bạn đã tham gia lớp này rồi");
        }

        LearningSpaceMember member = new LearningSpaceMember();
        member.setLearningSpace(space);
        member.setUser(currentUser);
        member.setRole(MemberRole.MEMBER);
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        JoinLearningSpaceResponse response = new JoinLearningSpaceResponse();
        response.setMessage("Tham gia lớp học thành công");
        response.setLearningSpaceId(space.getId());
        response.setLearningSpaceName(space.getName());
        response.setRole(MemberRole.MEMBER);
        return response;
    }

    @Override
    @Transactional
    public void deleteLearningSpace(Long id) {
        String username = getCurrentUsername();
        LearningSpace learningSpace = learningSpaceRepository
                .findByIdAndStatus(id, LearningSpaceStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Learning Space hoặc đã bị xóa"));

        if (!learningSpace.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa");
        }
        learningSpace.setStatus(LearningSpaceStatus.DELETE);
        learningSpaceRepository.save(learningSpace);
    }

    @Override
    @Transactional
    public void restoreLearningSpace(Long id) {
        String username = getCurrentUsername();
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy learning space"));

        if (!learningSpace.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Bạn không có quyền khôi phục learning space");
        }

        if (learningSpace.getStatus() != LearningSpaceStatus.DELETE) {
            throw new IllegalArgumentException("Lớp đang hoạt động, không cần khôi phục");
        }

        learningSpace.setStatus(LearningSpaceStatus.ACTIVE);
        learningSpaceRepository.save(learningSpace);
    }

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
}
