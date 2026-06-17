package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateNodeCommentRequest;
import com.example.flippedclass.dto.response.NodeCommentResponse;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.NodeComment;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.NodeCommentRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.NodeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NodeCommentServiceImpl implements NodeCommentService {

    private final NodeCommentRepository nodeCommentRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final UserRepository userRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NodeCommentResponse> getCommentsByNodeId(Long nodeId, Long userId) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NotFoundException("Learning node not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        checkClassroomMembership(node, user);

        List<NodeComment> allComments = nodeCommentRepository.findByLearningNodeIdOrderByCreatedAtAsc(nodeId);

        java.util.Map<Long, NodeCommentResponse> responseMap = new java.util.HashMap<>();
        List<NodeCommentResponse> rootComments = new java.util.ArrayList<>();

        for (NodeComment comment : allComments) {
            NodeCommentResponse dto = mapToResponse(comment);
            responseMap.put(dto.getId(), dto);

            if (comment.getParentComment() == null) {
                rootComments.add(0, dto); // Prepend to reverse ASC to DESC
            } else {
                NodeCommentResponse parentDto = responseMap.get(comment.getParentComment().getId());
                if (parentDto != null) {
                    parentDto.getReplies().add(dto); // Append to keep ASC
                }
            }
        }

        return rootComments;
    }

    @Override
    @Transactional
    public NodeCommentResponse createComment(Long nodeId, Long userId, CreateNodeCommentRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NotFoundException("Learning node not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        checkClassroomMembership(node, user);

        NodeComment comment = NodeComment.builder()
                .learningNode(node)
                .user(user)
                .content(request.getContent())
                .build();

        comment = nodeCommentRepository.save(comment);

        return mapToResponse(comment);
    }

    @Override
    @Transactional
    public NodeCommentResponse replyToComment(Long nodeId, Long parentCommentId, Long userId, CreateNodeCommentRequest request) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NotFoundException("Learning node not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        NodeComment parentComment = nodeCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new NotFoundException("Parent comment not found"));

        if (!parentComment.getLearningNode().getId().equals(nodeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment does not belong to this node");
        }

        if (parentComment.getParentComment() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reply to a reply. Only one level of replies is allowed.");
        }

        checkClassroomMembership(node, user);

        NodeComment reply = NodeComment.builder()
                .learningNode(node)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        reply = nodeCommentRepository.save(reply);

        return mapToResponse(reply);
    }

    @Override
    @Transactional
    public NodeCommentResponse editComment(Long commentId, Long userId, CreateNodeCommentRequest request) {
        NodeComment comment = nodeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own comments");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        comment = nodeCommentRepository.save(comment);

        return mapToResponse(comment);
    }

    @Override
    @Transactional
    public void deleteOwnComment(Long commentId, Long userId) {
        NodeComment comment = nodeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments");
        }

        nodeCommentRepository.delete(comment);
    }

    private void checkClassroomMembership(LearningNode node, User user) {
        var learningSpace = node.getLearningPath().getLearningSpace();
        boolean isMember = learningSpaceMemberRepository.existsByLearningSpaceAndUser(learningSpace, user);
        
        boolean isOwner = learningSpace.getOwner().getId().equals(user.getId());

        if (!isMember && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of the classroom containing this node");
        }
    }

    private NodeCommentResponse mapToResponse(NodeComment comment) {
        return NodeCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .fullName(comment.getUser().getFullName())
                .avatarUrl(comment.getUser().getAvatarUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
