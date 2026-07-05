package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.DiscussionRequest;
import com.example.flippedclass.dto.response.DiscussionResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.NodeDiscussion;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.DiscussionStatus;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.NodeDiscussionRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.repository.StudyGroupRepository;
import com.example.flippedclass.entity.StudyGroup;
import com.example.flippedclass.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {

    private final NodeDiscussionRepository discussionRepository;
    private final LearningNodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository groupRepository;
    private final com.example.flippedclass.repository.NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionResponse> getDiscussionsByNodeId(Long nodeId, Long groupId) {
        List<NodeDiscussion> rootDiscussions;
        if (groupId != null) {
            rootDiscussions = discussionRepository.findRootDiscussionsByNodeIdAndGroupId(nodeId, groupId);
        } else {
            rootDiscussions = discussionRepository.findRootDiscussionsByNodeId(nodeId);
        }
        return rootDiscussions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiscussionResponse addDiscussion(Long nodeId, Long groupId, String username, DiscussionRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LearningNode node = nodeRepository.findById(nodeId).orElseThrow(() -> new RuntimeException("Node not found"));

        NodeDiscussion discussion = NodeDiscussion.builder()
                .learningNode(node)
                .user(user)
                .content(request.getContent())
                .build();

        if (groupId != null) {
            StudyGroup group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            discussion.setStudyGroup(group);
        }

        if (request.getParentId() != null) {
            NodeDiscussion parent = discussionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent discussion not found"));
            discussion.setParentDiscussion(parent);
        }

        NodeDiscussion saved = discussionRepository.save(discussion);

        // Trigger notification if this is a reply to someone else
        if (saved.getParentDiscussion() != null) {
            User parentAuthor = saved.getParentDiscussion().getUser();
            if (!parentAuthor.getId().equals(user.getId())) {
                boolean isLecturer = parentAuthor.getRoles().stream()
                        .anyMatch(r -> r.getName().name().equals("MENTOR"));
                String targetUrl;
                
                if (isLecturer) {
                    targetUrl = "/lecturer/learning-nodes/" + node.getId() + "/preview";
                } else {
                    targetUrl = "/student/learning-node?nodeId=" + node.getId() + "&commentId=" + saved.getId();
                    if (node.getLearningPath() != null && node.getLearningPath().getLearningSpace() != null) {
                        targetUrl += "&spaceId=" + node.getLearningPath().getLearningSpace().getId();
                    }
                }
                
                com.example.flippedclass.entity.Notification notification = com.example.flippedclass.entity.Notification.builder()
                        .recipient(parentAuthor)
                        .type(com.example.flippedclass.enums.NotificationType.COMMENT_REPLY)
                        .message(user.getFullName() + " replied to your comment.")
                        .targetUrl(targetUrl)
                        .build();
                notificationRepository.save(notification);
            }
        }

        messagingTemplate.convertAndSend("/topic/nodes/" + nodeId + "/comments", "REFRESH");

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DiscussionResponse markAsSolved(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateLecturerOrAdmin(user);
        
        discussion.setStatus(discussion.getStatus() == DiscussionStatus.OPEN ? DiscussionStatus.SOLVED : DiscussionStatus.OPEN);
        discussion = discussionRepository.save(discussion);
        
        messagingTemplate.convertAndSend("/topic/nodes/" + discussion.getLearningNode().getId() + "/comments", "REFRESH");
        
        return mapToResponse(discussion);
    }

    @Override
    @Transactional
    public DiscussionResponse togglePin(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateLecturerOrAdmin(user);

        discussion.setPinned(!discussion.isPinned());
        discussion = discussionRepository.save(discussion);
        
        messagingTemplate.convertAndSend("/topic/nodes/" + discussion.getLearningNode().getId() + "/comments", "REFRESH");
        
        return mapToResponse(discussion);
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateDiscussionOwnerOrLecturer(discussion, user);

        discussionRepository.delete(discussion);
        notificationRepository.deleteByTargetUrlContaining("commentId=" + discussionId);
    }

    private void validateDiscussionOwnerOrLecturer(NodeDiscussion discussion, User currentUser) {
        boolean isOwner = discussion.getUser().getId().equals(currentUser.getId());
        boolean isLecturerOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("MENTOR") || role.getName().name().equals("ADMIN"));
        if (!isOwner && !isLecturerOrAdmin) {
            throw new com.example.flippedclass.exception.BusinessException("FORBIDDEN: You do not have permission to modify this discussion.");
        }
    }

    private void validateLecturerOrAdmin(User currentUser) {
        boolean isLecturerOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("MENTOR") || role.getName().name().equals("ADMIN"));
        if (!isLecturerOrAdmin) {
            throw new com.example.flippedclass.exception.BusinessException("FORBIDDEN: You do not have permission to perform this action.");
        }
    }

    private DiscussionResponse mapToResponse(NodeDiscussion discussion) {
        String roleStr = "STUDENT";
        if (discussion.getUser().getRoles() != null) {
            for (Role role : discussion.getUser().getRoles()) {
                if (role.getName().name().equalsIgnoreCase("MENTOR")) roleStr = "LECTURER";
                else if (role.getName().name().equalsIgnoreCase("SUPPORTER")) roleStr = "SUPPORTER";
            }
        }
        
        List<DiscussionResponse> replyResponses = null;
        if (discussion.getReplies() != null && !discussion.getReplies().isEmpty()) {
            replyResponses = discussion.getReplies().stream()
                .sorted(java.util.Comparator.comparing(NodeDiscussion::getCreatedAt))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        }

        return DiscussionResponse.builder()
                .id(discussion.getId())
                .content(discussion.getContent())
                .authorName(discussion.getUser().getFullName() != null ? discussion.getUser().getFullName() : discussion.getUser().getUsername())
                .authorAvatar(discussion.getUser().getAvatarUrl())
                .authorRole(roleStr)
                .authorId(discussion.getUser().getId())
                .status(discussion.getStatus())
                .isPinned(discussion.isPinned())
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt())
                .replies(replyResponses)
                .replyCount(discussion.getReplies() != null ? discussion.getReplies().size() : 0)
                .build();
    }
}
