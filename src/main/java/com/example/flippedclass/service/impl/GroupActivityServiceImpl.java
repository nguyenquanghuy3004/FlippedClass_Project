package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.GroupActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.GroupActivityResponse;
import com.example.flippedclass.entity.GroupActivity;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.GroupActivityRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.GroupActivityService;
import com.example.flippedclass.validation.GroupActivityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupActivityServiceImpl implements GroupActivityService {

    private final GroupActivityRepository activityRepository;
    private final LearningNodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final GroupActivityValidator validator;

    @Override
    @Transactional
    public GroupActivityResponse createActivity(Long learningNodeId, Long currentUserId, GroupActivityCreateRequest request) {
        LearningNode node = nodeRepository.findById(learningNodeId)
                .orElseThrow(() -> new NotFoundException("Learning node not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupActivity activity = GroupActivity.builder()
                .learningNode(node)
                .title(request.getTitle())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 4)
                .deadline(request.getDeadline())
                .status(ActivityStatus.DRAFT)
                .createdBy(currentUser)
                .build();

        activity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupActivityResponse getActivityDetails(Long activityId) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupActivityResponse getActivityByNodeId(Long nodeId) {
        GroupActivity activity = activityRepository.findByLearningNodeId(nodeId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Group Activity not found for this node"));
        return mapToResponse(activity);
    }

    @Override
    @Transactional
    public void publishActivity(Long activityId) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        activity.setStatus(ActivityStatus.OPEN);
        activityRepository.save(activity);
    }

    @Override
    @Transactional
    public void closeActivity(Long activityId) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        activity.setStatus(ActivityStatus.LOCKED);
        activityRepository.save(activity);
    }

    private GroupActivityResponse mapToResponse(GroupActivity activity) {
        return GroupActivityResponse.builder()
                .id(activity.getId())
                .learningNodeId(activity.getLearningNode().getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .maxMembers(activity.getMaxMembers())
                .deadline(activity.getDeadline())
                .status(activity.getStatus())
                .build();
    }
}
