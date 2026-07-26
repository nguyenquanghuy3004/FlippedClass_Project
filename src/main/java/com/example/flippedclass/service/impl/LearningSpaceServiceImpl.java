package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.response.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.response.LearningSpaceResponse;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.enums.*;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.service.LearningSpaceService;
import com.example.flippedclass.service.InviteCodeGenerator;
import com.example.flippedclass.util.ValidateJoinLearningSpace;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import com.example.flippedclass.service.LearningPathService;

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
    private final LearningPathService learningPathService;


    private User getCurrentUser() {
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MENTOR"));
    }


    @Transactional
    @Override
    public LearningSpaceResponse createLearningSpace(CreateLearningSpaceRequest request) {

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Learning Space name cannot be empty");
        }

        User owner = getCurrentUser();

        // Security check for STUDENTs: Must be a SUPPORTER in at least one space
        if (owner.getRoles().stream().noneMatch(r -> r.getName() == RoleName.MENTOR || r.getName() == RoleName.ADMIN)) {
            if (!memberRepository.existsByUser_IdAndRole(owner.getId(), MemberRole.SUPPORTER)) {
                throw new IllegalArgumentException("Only promoted students (Supporter) have the right to create a Learning Space.");
            }
        }

        // Prevent duplicate space names for the same owner
        List<LearningSpace> existingSpaces = learningSpaceRepository.findByOwnerIdAndStatus(owner.getId(), LearningSpaceStatus.ACTIVE);
        boolean nameExists = existingSpaces.stream().anyMatch(s -> s.getName().equalsIgnoreCase(request.getName().trim()));
        if (nameExists) {
            throw new IllegalArgumentException("You already have a Mentoring Space with this name. Please choose another name!");
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
        List<PeerPairing> mentees = peerPairingRepository.findByMentor_Id(owner.getId());
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
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ADMIN);
        boolean isMentor = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.MENTOR);

        List<LearningSpace> spaces;
        if (isAdmin) {
            spaces = learningSpaceRepository.findAll();
        } else if (isMentor) {
            spaces = learningSpaceRepository.findByOwnerId(currentUser.getId());
        } else {
            List<LearningSpace> ownedSpaces = learningSpaceRepository.findByOwnerId(currentUser.getId());
            List<LearningSpace> joinedSpaces = memberRepository.findByUser_IdOrderByJoinedAtDesc(currentUser.getId()).stream()
                    .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                    .map(LearningSpaceMember::getLearningSpace)
                    .filter(s -> s.getStatus() == LearningSpaceStatus.ACTIVE)
                    .collect(Collectors.toList());

            Set<LearningSpace> allSpaces = new HashSet<>(ownedSpaces);
            allSpaces.addAll(joinedSpaces);
            spaces = new java.util.ArrayList<>(allSpaces);
        }

        return spaces.stream().map(space -> LearningSpaceResponse.builder()
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
    @Transactional
    public List<LearningSpaceResponse> getPublicSpaces() {
        return learningSpaceRepository
                .findByVisibilityAndStatus(VisibilityType.PUBLIC, LearningSpaceStatus.ACTIVE)
                .stream()
                .filter(space -> space.getOwner().getRoles().stream()
                        .anyMatch(role -> role.getName() == RoleName.MENTOR))
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
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code or the space has been deleted"));

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
        LearningSpace space = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Learning Space not found or has been deleted"));

        // ADMIN bypasses owner check
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!space.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("You don't have permission to update this Learning Space");
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
                    .orElseThrow(() -> new IllegalArgumentException("Invalid invite code or the space has been deleted"));
        } else if (request.getSpaceId() != null) {
            learningSpace = learningSpaceRepository.findByIdAndStatus(request.getSpaceId(), LearningSpaceStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("Learning Space not found"));

            if (learningSpace.getVisibility() == VisibilityType.PRIVATE) {
                throw new IllegalArgumentException("This space is private, you need an invite code to join");
            }
        } else {
            throw new IllegalArgumentException("Please provide an invite code or space ID");
        }

        User currentUser = getCurrentUser();

        if (memberRepository.existsByLearningSpaceAndUser(learningSpace, currentUser)) {
            throw new IllegalArgumentException("You have already joined this space");
        }

        LearningSpaceMember member = new LearningSpaceMember();
        member.setLearningSpace(learningSpace);
        member.setUser(currentUser);
        member.setRole(MemberRole.MEMBER);
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        // Return Response
        JoinLearningSpaceResponse response = new JoinLearningSpaceResponse();
        response.setMessage("Successfully joined the space!");
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
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Space not found"));

        if (learningSpace.getStatus() == LearningSpaceStatus.DELETE) {
            throw new IllegalArgumentException("Learning Space has already been deleted");
        }

        if (learningSpace.getStatus() == LearningSpaceStatus.ARCHIVE) {
            throw new IllegalArgumentException("This space is archived and cannot be deleted. Please unarchive it first to delete.");
        }

        // ADMIN bypasses owner check
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("You don't have permission to delete");
            }
        }

        // ==========================================
        // CÁCH 1: XÓA MỀM (Code cũ ban đầu)
        // ==========================================
        learningSpace.setStatus(LearningSpaceStatus.DELETE);
        learningSpaceRepository.save(learningSpace);

        // ==========================================
        // CÁCH 2: XÓA CỨNG (Đang dùng để test)
        // ==========================================
        // // 1. Delete all paths (which will delete their nodes)
        // if (learningSpace.getPaths() != null) {
        //     List<com.example.flippedclass.entity.LearningPath> pathsToDel = new java.util.ArrayList<>(learningSpace.getPaths());
        //     
        //     for (com.example.flippedclass.entity.LearningPath path : pathsToDel) {
        //         learningPathService.deleteLearningPathModul(id, path.getId());
        //     }
        //     
        //     learningSpace.getPaths().clear();
        // }
        // 
        // // 2. Delete all members
        // if (learningSpace.getMembers() != null) {
        //     learningSpace.getMembers().clear();
        //     memberRepository.deleteAllByLearningSpaceId(id);
        // }
        // 
        // // 3. Hard delete the space
        // learningSpaceRepository.delete(learningSpace);
    }

    // restore learning Space
    @Override
    @Transactional
    public void restoreLearningSpace(Long id) {
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Space not found"));

        // ADMIN bypasses owner check
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("You don't have permission to restore this Learning Space");
            }
        }

        if (learningSpace.getStatus() == LearningSpaceStatus.ACTIVE) {
            throw new IllegalArgumentException("The space is currently active, no need to restore");
        }

        learningSpace.setStatus(LearningSpaceStatus.ACTIVE);
        learningSpaceRepository.save(learningSpace);
    }

    // Archive learning Space
    @Override
    @Transactional
    public void archiveLearningSpace(Long id) {
        LearningSpace learningSpace = learningSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Space not found"));

        // ADMIN bypasses owner check
        if (!isCurrentUserAdmin()) {
            String username = getCurrentUsername();
            if (!learningSpace.getOwner().getUsername().equals(username)) {
                throw new IllegalArgumentException("You don't have permission to archive this Learning Space");
            }
        }

        if (learningSpace.getStatus() == LearningSpaceStatus.ARCHIVE) {
            throw new IllegalArgumentException("The space is already archived");
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
                // Bỏ qua Group Activity, chỉ copy Video, Document, Practice, Quiz
                if ("GROUP_ACTIVITY".equals(node.getNodeType())) {
                    continue;
                }
                
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

               Map<Long, Long> quizIdMap = new HashMap<>();

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
                    quizIdMap.put(quiz.getId(), savedQuiz.getId());

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

                for (LearningNodeItem nodeItem : node.getItems()) {
                    LearningNodeItem newItem = new LearningNodeItem();

                    newItem.setTitle(nodeItem.getTitle());
                    newItem.setItemType(nodeItem.getItemType());
                    newItem.setUrl(nodeItem.getUrl());
                    newItem.setContent(nodeItem.getContent());
                    newItem.setPosition(nodeItem.getPosition());
                    newItem.setLearningNode(saveNode);

                    if (nodeItem.getItemType() == ItemType.QUIZ && nodeItem.getQuizId() != null) {
                        newItem.setQuizId(quizIdMap.get(nodeItem.getQuizId()));
                    }

                    learningNodeItemRepository.save(newItem);
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
