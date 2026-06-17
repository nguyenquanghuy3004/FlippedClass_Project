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
import com.example.flippedclass.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {

    private final NodeDiscussionRepository discussionRepository;
    private final LearningNodeRepository nodeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionResponse> getDiscussionsByNodeId(Long nodeId) {
        List<NodeDiscussion> rootDiscussions = discussionRepository.findRootDiscussionsByNodeId(nodeId);
        return rootDiscussions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiscussionResponse addDiscussion(Long nodeId, String username, DiscussionRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LearningNode node = nodeRepository.findById(nodeId).orElseThrow(() -> new RuntimeException("Node not found"));

        NodeDiscussion discussion = NodeDiscussion.builder()
                .learningNode(node)
                .user(user)
                .content(request.getContent())
                .build();

        if (request.getParentId() != null) {
            NodeDiscussion parent = discussionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent discussion not found"));
            discussion.setParentDiscussion(parent);
        }

        NodeDiscussion saved = discussionRepository.save(discussion);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DiscussionResponse markAsSolved(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        // TODO: verify user is lecturer/mentor
        discussion.setStatus(discussion.getStatus() == DiscussionStatus.OPEN ? DiscussionStatus.SOLVED : DiscussionStatus.OPEN);
        return mapToResponse(discussionRepository.save(discussion));
    }

    @Override
    @Transactional
    public DiscussionResponse togglePin(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        // TODO: verify user is lecturer/mentor
        discussion.setPinned(!discussion.isPinned());
        return mapToResponse(discussionRepository.save(discussion));
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long discussionId, String username) {
        NodeDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        // TODO: verify user
        discussionRepository.delete(discussion);
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
