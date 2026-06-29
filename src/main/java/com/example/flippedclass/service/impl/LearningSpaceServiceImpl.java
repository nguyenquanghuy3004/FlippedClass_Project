package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.response.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.response.LearningSpaceResponse;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.service.LearningSpaceService;
import com.example.flippedclass.service.InviteCodeGenerator;
import com.example.flippedclass.util.ValidateJoinLearningSpace;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.VisibilityType;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.enums.MemberStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningSpaceServiceImpl implements LearningSpaceService {

    private final LearningSpaceRepository learningSpaceRepository;
    private final LearningSpaceMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final InviteCodeGenerator inviteCodeGenerator;
    private final ValidateJoinLearningSpace validateJoinLearningSpace;
    private final com.example.flippedclass.repository.PeerPairingRepository peerPairingRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final LearningNodeItemRepository learningNodeItemRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final CourseDocumentServiceImpl courseDocumentServicel;
    private final LearningPathRepository learningPathRepository;
    private final CourseDocumentRepository courseDocumentRepository;


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

        // Security check for STUDENTs: Must be a SUPPORTER in at least one space
        if (owner.getRoles().stream().noneMatch(r -> r.getName() == com.example.flippedclass.enums.RoleName.MENTOR || r.getName() == com.example.flippedclass.enums.RoleName.ADMIN)) {
            if (!memberRepository.existsByUser_IdAndRole(owner.getId(), MemberRole.SUPPORTER)) {
                throw new IllegalArgumentException("Chỉ những sinh viên được thăng cấp (Supporter) mới có quyền tạo Learning Space.");
            }
        }

        // Prevent duplicate space names for the same owner
        List<LearningSpace> existingSpaces = learningSpaceRepository.findByOwnerIdAndStatus(owner.getId(), LearningSpaceStatus.ACTIVE);
        boolean nameExists = existingSpaces.stream().anyMatch(s -> s.getName().equalsIgnoreCase(request.getName().trim()));
        if (nameExists) {
            throw new IllegalArgumentException("Bạn đã có một Mentoring Space với tên này rồi. Vui lòng chọn tên khác!");
        }

        // Create Entity and save
        String inviteCode = inviteCodeGenerator.generateUniqueInviteCode();

        LearningSpace learningSpace = LearningSpace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .owner(owner)
                .inviteCode(inviteCode)
                .build();
        LearningSpace savedSpace = learningSpaceRepository.save(learningSpace);

        // tạo luôn OWNER trong bảng member
        LearningSpaceMember ownerMember = new LearningSpaceMember();
        ownerMember.setLearningSpace(savedSpace);
        ownerMember.setUser(owner);
        ownerMember.setRole(MemberRole.OWNER);
        ownerMember.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(ownerMember);

        // Auto-add mentees of this supporter to the newly created space
        List<com.example.flippedclass.entity.PeerPairing> mentees = peerPairingRepository.findByMentor_Id(owner.getId());
        for (com.example.flippedclass.entity.PeerPairing pairing : mentees) {
            if (pairing.getStatus() == com.example.flippedclass.enums.PeerPairingStatus.ACTIVE) {
                if (!memberRepository.existsByLearningSpaceAndUser(savedSpace, pairing.getMentee())) {
                    LearningSpaceMember menteeMember = new LearningSpaceMember();
                    menteeMember.setLearningSpace(savedSpace);
                    menteeMember.setUser(pairing.getMentee());
                    menteeMember.setRole(MemberRole.MEMBER);
                    menteeMember.setStatus(MemberStatus.ACTIVE);
                    memberRepository.save(menteeMember);
                }
            }
        }

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
        List<LearningSpace> ownedSpaces = learningSpaceRepository.findByOwnerId(currentUser.getId());
        List<LearningSpace> joinedSpaces = memberRepository.findByUser_IdOrderByJoinedAtDesc(currentUser.getId()).stream()
                .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                .map(LearningSpaceMember::getLearningSpace)
                .collect(Collectors.toList());

        java.util.Set<LearningSpace> allSpaces = new java.util.HashSet<>(ownedSpaces);
        allSpaces.addAll(joinedSpaces);

        return allSpaces.stream().map(space -> LearningSpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .ownerId(space.getOwner().getId())
                .ownerUsername(space.getOwner().getUsername())
                .createdAt(space.getCreatedAt())
                .inviteCode(space.getInviteCode())
                .visibility(space.getVisibility())
                .status(space.getStatus())
                .build()).collect(Collectors.toList());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<LearningSpaceResponse> getPublicSpaces() {
        return learningSpaceRepository
                .findByVisibilityAndStatus(VisibilityType.PUBLIC, LearningSpaceStatus.ACTIVE)
                .stream()
                .map(space -> LearningSpaceResponse.builder()
                        .id(space.getId())
                        .name(space.getName())
                        .description(space.getDescription())
                        .inviteCode(space.getInviteCode())
                        .ownerId(space.getOwner().getId())
                        .ownerUsername(space.getOwner().getUsername())
                        .visibility(space.getVisibility())
                        .createdAt(space.getCreatedAt())
                        .status(space.getStatus())
                        .build())
                .toList();
    }

    @Override
    public LearningSpaceResponse getSpaceByInviteCode(String inviteCode) {
        String normalizedInviteCode = inviteCode == null ? "" : inviteCode.trim();
        LearningSpace learningSpace = learningSpaceRepository.findByInviteCodeIgnoreCaseAndStatus(normalizedInviteCode, LearningSpaceStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Mã mời không hợp lệ hoặc lớp đã bị xóa"));

        return LearningSpaceResponse.builder()
                .id(learningSpace.getId())
                .name(learningSpace.getName())
                .description(learningSpace.getDescription())
                .inviteCode(learningSpace.getInviteCode())
                .visibility(learningSpace.getVisibility())
                .ownerId(learningSpace.getOwner().getId())
                .ownerUsername(learningSpace.getOwner().getUsername())
                .createdAt(learningSpace.getCreatedAt())
                .status(learningSpace.getStatus())
                .build();
    }

    @Transactional
    @Override
    public LearningSpace updateLearningSpace(Long id, LearningSpace spaceDetail) {
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


    // joint class
    @Override
    @Transactional
    public JoinLearningSpaceResponse joinLearningSpace(JoinLearningSpaceRequest request) {
        LearningSpace learningSpace;

        String inviteCode = request.getInviteCode() == null ? "" : request.getInviteCode().trim();

        if (!inviteCode.isEmpty()) {
            learningSpace = learningSpaceRepository.findByInviteCodeIgnoreCaseAndStatus(inviteCode, LearningSpaceStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("Mã mời không hợp lệ hoặc lớp đã bị xóa"));
        } else if (request.getSpaceId() != null) {
            learningSpace = learningSpaceRepository.findByIdAndStatus(request.getSpaceId(), LearningSpaceStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học"));

            if (learningSpace.getVisibility() == VisibilityType.PRIVATE) {
                throw new IllegalArgumentException("Lớp học này là riêng tư, bạn phải có mã mời để tham gia");
            }
        } else {
            throw new IllegalArgumentException("Vui lòng cung cấp mã mời hoặc ID lớp học");
        }

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

    // delete learning Space
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

        // Không cho phép xóa nếu đã có học viên (MEMBER) tham gia
        long memberCount = memberRepository.countByLearningSpaceIdAndRole(id, MemberRole.MEMBER);
        if (memberCount > 0) {
            throw new IllegalArgumentException("Không thể xóa Learning Space này vì đã có học viên (Mentee) tham gia.");
        }

        learningSpace.setStatus(LearningSpaceStatus.DELETE);
        learningSpaceRepository.save(learningSpace);
    }

    // restore learning Space
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

    // Archive learning Space
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


    // clone learning Space
    @Override
    @Transactional
    public LearningSpaceResponse cloneSpace(Long sourseSpaceId, String newName) {
        LearningSpace soureSpace = learningSpaceRepository.findById(sourseSpaceId)
                .orElseThrow(() -> new IllegalArgumentException("Original space not found"));

        LearningSpace newSpace = new LearningSpace();
        newSpace.setName(newName);
        newSpace.setDescription(soureSpace.getDescription());
        newSpace.setVisibility(soureSpace.getVisibility());
        newSpace.setStatus(LearningSpaceStatus.ACTIVE);
        newSpace.setOwner(getCurrentUser());
        newSpace.setInviteCode(inviteCodeGenerator.generateInviteCode());

        LearningSpace saveSpace = learningSpaceRepository.save(newSpace);

        // Tạo OWNER trong bảng member
        LearningSpaceMember ownerMember = new LearningSpaceMember();
        ownerMember.setLearningSpace(saveSpace);
        ownerMember.setUser(getCurrentUser());
        ownerMember.setRole(MemberRole.OWNER);
        ownerMember.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(ownerMember);

        List<LearningPath> sourcePath = learningPathRepository.findByLearningSpace_IdOrderByPositionAsc(sourseSpaceId);

        for (LearningPath path : sourcePath) {
            LearningPath newPath = new LearningPath();

            newPath.setTitle(path.getTitle());
            newPath.setDescription(path.getDescription());
            newPath.setPosition(path.getPosition());
            newPath.setEstimatedDurationHours(path.getEstimatedDurationHours());
            newPath.setVisibility(path.getVisibility());
            newPath.setStatus(path.getStatus());
            newPath.setLecturer(getCurrentUser());
            newPath.setLearningSpace(saveSpace);

            LearningPath savePath = learningPathRepository.save(newPath);


            for (CourseDocument sDoc : path.getCourseDocuments()) {
                CourseDocument nDoc = new CourseDocument();

                nDoc.setTitle(sDoc.getTitle());
                nDoc.setDescription(sDoc.getDescription());
                nDoc.setDocumentType(sDoc.getDocumentType());
                nDoc.setUrl(sDoc.getUrl());
                nDoc.setLearningPath(savePath);
                courseDocumentRepository.save(nDoc);
            }

            for (LearningNode node : path.getNodes()) {
                LearningNode newNode = new LearningNode();

                newNode.setTitle(node.getTitle());
                newNode.setDescription(node.getDescription());
                newNode.setContent(node.getContent());
                newNode.setNodeType(node.getNodeType());
                newNode.setStatus(node.getStatus());
                newNode.setDisplayOrder(node.getDisplayOrder());
                newNode.setEstimatedMinutes(node.getEstimatedMinutes());
                newNode.setLearningPath(savePath);

                LearningNode saveNode = learningNodeRepository.save(newNode);

                for (LearningNodeItem nodeItem : node.getItems()) {
                    LearningNodeItem newItem = new LearningNodeItem();

                    newItem.setTitle(nodeItem.getTitle());
                    newItem.setItemType(nodeItem.getItemType());
                    newItem.setUrl(nodeItem.getUrl());
                    newItem.setContent(nodeItem.getContent());
                    newItem.setPosition(nodeItem.getPosition());
                    newItem.setLearningNode(saveNode);
                    learningNodeItemRepository.save(newItem);
                }

                for (Quiz quiz : node.getQuizzes()) {
                    Quiz newQuiz = new Quiz();

                    newQuiz.setTitle(quiz.getTitle());
                    newQuiz.setDescription(quiz.getDescription());
                    newQuiz.setDurationMinutes(quiz.getDurationMinutes());
                    newQuiz.setActive(quiz.isActive());
                    newQuiz.setPassScore(quiz.getPassScore());
                    newQuiz.setDifficulty(quiz.getDifficulty());
                    newQuiz.setThumbnailUrl(quiz.getThumbnailUrl());
                    newQuiz.setLecturer(getCurrentUser());
                    newQuiz.setLearningNode(saveNode);

                    Quiz savedQuiz = quizRepository.save(newQuiz);
                    // Copy câu hỏi của Quiz (Phải viết query riêng vì Quiz không map trực tiếp Questions trong Entity)
                    List<QuizQuestion> sourceQuestions = quizQuestionRepository.findByQuizId(quiz.getId());

                    for (QuizQuestion question : sourceQuestions) {
                        QuizQuestion newQuestion = new QuizQuestion();

                        newQuestion.setContent(question.getContent());
                        newQuestion.setOptions(question.getOptions());
                        newQuestion.setCorrectAnswer(question.getCorrectAnswer());
                        newQuestion.setPoints(question.getPoints());
                        newQuestion.setQuestionType(question.getQuestionType());
                        newQuestion.setSortOrder(question.getSortOrder());
                        newQuestion.setQuiz(savedQuiz);
                        quizQuestionRepository.save(newQuestion);

                    }
                }
            }
        }
        return LearningSpaceResponse.builder()
                .id(saveSpace.getId())
                .name(saveSpace.getName())
                .description(saveSpace.getDescription())
                .inviteCode(saveSpace.getInviteCode())
                .visibility(saveSpace.getVisibility())
                .ownerId(saveSpace.getOwner().getId())
                .ownerUsername(saveSpace.getOwner().getUsername())
                .createdAt(saveSpace.getCreatedAt())
                .status(saveSpace.getStatus())
                .build();
    }
}
