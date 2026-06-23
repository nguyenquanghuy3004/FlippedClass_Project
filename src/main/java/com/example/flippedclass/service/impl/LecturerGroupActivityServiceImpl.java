package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;
import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;
import com.example.flippedclass.entity.GroupActivity;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.GroupActivityRepository;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.LecturerGroupActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerGroupActivityServiceImpl implements LecturerGroupActivityService {

    private final GroupActivityRepository activityRepository;
    private final LearningNodeRepository nodeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LecturerActivityResponse createActivity(Long currentUserId, LecturerActivityCreateRequest request) {
        LearningNode node = nodeRepository.findById(request.getLearningNodeId())
                .orElseThrow(() -> new NotFoundException("Learning node not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupActivity activity = GroupActivity.builder()
                .learningNode(node)
                .title(request.getTitle())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 4)
                .deadline(request.getDeadline())
                .status(ActivityStatus.OPEN) // Default to OPEN or DRAFT based on requirements
                .createdBy(currentUser)
                .build();

        activity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerActivityResponse> listActivitiesBySpaceId(Long spaceId) {
        List<GroupActivity> activities = activityRepository.findBySpaceId(spaceId);
        return activities.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerActivityResponse getActivityDetails(Long activityId) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Group activity not found"));
        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerGroupResponse> listGroupsByActivityId(Long activityId, String search, String filter) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Group activity not found"));

        return activity.getGroups().stream()
                .filter(g -> {
                    boolean matchSearch = true;
                    if (search != null && !search.trim().isEmpty()) {
                        String s = search.toLowerCase();
                        matchSearch = g.getGroupName().toLowerCase().contains(s) ||
                                (g.getLeader() != null && g.getLeader().getFullName().toLowerCase().contains(s));
                    }
                    boolean matchFilter = true;
                    if (filter != null && !filter.trim().isEmpty() && !filter.equalsIgnoreCase("All")) {
                        boolean isSubmitted = g.getSubmission() != null;
                        if (filter.equalsIgnoreCase("SUBMITTED")) matchFilter = isSubmitted;
                        else if (filter.equalsIgnoreCase("PENDING")) matchFilter = !isSubmitted;
                    }
                    return matchSearch && matchFilter;
                })
                .map(g -> {
                    String status = "PENDING";
                    String githubUrl = null;
                    if (g.getSubmission() != null) {
                        status = g.getSubmission().getStatus().name();
                        githubUrl = g.getSubmission().getGithubRepoUrl();
                    }
                    return LecturerGroupResponse.builder()
                            .groupId(g.getId())
                            .groupName(g.getGroupName())
                            .leaderName(g.getLeader() != null ? g.getLeader().getFullName() : "Unknown")
                            .currentMembers(g.getMembers() != null ? g.getMembers().size() : 0)
                            .maxMembers(activity.getMaxMembers())
                            .status(status)
                            .githubUrl(githubUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private LecturerActivityResponse mapToResponse(GroupActivity activity) {
        int totalGroups = activity.getGroups() != null ? activity.getGroups().size() : 0;
        
        int totalStudents = 0;
        int submittedGroups = 0;
        
        if (activity.getGroups() != null) {
            totalStudents = activity.getGroups().stream()
                    .mapToInt(g -> g.getMembers() != null ? g.getMembers().size() : 0)
                    .sum();
            
            submittedGroups = (int) activity.getGroups().stream()
                    .filter(g -> g.getSubmission() != null)
                    .count();
        }

        return LecturerActivityResponse.builder()
                .id(activity.getId())
                .learningNodeId(activity.getLearningNode() != null ? activity.getLearningNode().getId() : null)
                .learningNodeTitle(activity.getLearningNode() != null ? activity.getLearningNode().getTitle() : null)
                .title(activity.getTitle())
                .description(activity.getDescription())
                .maxMembers(activity.getMaxMembers())
                .deadline(activity.getDeadline())
                .status(activity.getStatus())
                .totalGroups(totalGroups)
                .totalStudents(totalStudents)
                .submittedGroups(submittedGroups)
                .build();
    }
}
