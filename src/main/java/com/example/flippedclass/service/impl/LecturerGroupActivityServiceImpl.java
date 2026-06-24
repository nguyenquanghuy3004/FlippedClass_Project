package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.LecturerActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.LecturerActivityUpdateRequest;
import com.example.flippedclass.dto.request.activity.LecturerGradeRequest;
import com.example.flippedclass.dto.response.activity.LecturerActivityResponse;
import com.example.flippedclass.dto.response.activity.LecturerGroupResponse;
import com.example.flippedclass.dto.response.activity.LecturerReviewResponse;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.*;
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
    private final StudyGroupRepository groupRepository;
    private final ActivitySubmissionRepository submissionRepository;
    private final LearningPathRepository pathRepository;

    @Override
    @Transactional
    public LecturerActivityResponse createActivity(Long currentUserId, LecturerActivityCreateRequest request) {
        LearningPath path = pathRepository.findById(request.getLearningPathId())
                .orElseThrow(() -> new NotFoundException("Learning path not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Create a new LearningNode automatically
        LearningNode node = LearningNode.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .learningPath(path)
                .status("ACTIVE")
                .nodeType("GROUP_ACTIVITY")
                .build();
        node = nodeRepository.save(node);

        GroupActivity activity = GroupActivity.builder()
                .learningNode(node)
                .learningSpace(path.getLearningSpace())
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
    @Transactional
    public LecturerActivityResponse updateActivity(Long lecturerId, Long activityId, LecturerActivityUpdateRequest request) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        
        // Optional: Check if lecturer has access to this space/activity
        // Since it's a mentor, we can skip explicit check or add it if needed.
        
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setMaxMembers(request.getMaxMembers());
        activity.setDeadline(request.getDeadline());

        if (request.getLearningPathId() != null) {
            LearningNode node = activity.getLearningNode();
            if (node != null && (node.getLearningPath() == null || !node.getLearningPath().getId().equals(request.getLearningPathId()))) {
                LearningPath path = pathRepository.findById(request.getLearningPathId())
                        .orElseThrow(() -> new NotFoundException("Learning path not found"));
                node.setLearningPath(path);
                nodeRepository.save(node);
                activity.setLearningSpace(path.getLearningSpace());
            }
        }
        
        activityRepository.save(activity);
        
        return mapToResponse(activity);
    }

    @Override
    @Transactional
    public LecturerActivityResponse updateActivityStatus(Long lecturerId, Long activityId, String newStatus) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
                
        try {
            activity.setStatus(ActivityStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        
        activityRepository.save(activity);
        
        return mapToResponse(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long lecturerId, Long activityId) {
        GroupActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        
        LearningNode node = activity.getLearningNode();
        
        // Delete activity (which should cascade delete groups and submissions if configured correctly, or we can just rely on JPA)
        activityRepository.delete(activity);
        
        // Also delete the learning node since they are tightly coupled
        if (node != null) {
            nodeRepository.delete(node);
        }
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

    @Override
    @Transactional(readOnly = true)
    public LecturerReviewResponse getGroupReview(Long groupId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        return mapToReviewResponse(group);
    }

    @Override
    @Transactional
    public LecturerReviewResponse gradeGroup(Long currentUserId, Long groupId, LecturerGradeRequest request) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));
        User lecturer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ActivitySubmission submission = group.getSubmission();
        if (submission == null) {
            submission = new ActivitySubmission();
            submission.setGroup(group);
            submission.setStatus(SubmissionStatus.NOT_SUBMITTED);
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setUpdatedBy(lecturer);
        submission.setUpdatedAt(java.time.LocalDateTime.now());

        submissionRepository.save(submission);
        group.setSubmission(submission); // Update relationship in memory
        
        return mapToReviewResponse(group);
    }

    private LecturerReviewResponse mapToReviewResponse(StudyGroup group) {
        ActivitySubmission submission = group.getSubmission();
        String githubUrl = null;
        String submittedBy = null;
        java.time.LocalDateTime submittedAt = null;
        Double score = null;
        String feedback = null;
        String status = "NOT_SUBMITTED";

        if (submission != null) {
            githubUrl = submission.getGithubRepoUrl();
            submittedBy = submission.getSubmittedBy() != null ? submission.getSubmittedBy().getFullName() : null;
            submittedAt = submission.getSubmittedAt();
            score = submission.getScore();
            feedback = submission.getFeedback();
            status = submission.getStatus().name();
        }

        return LecturerReviewResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .githubUrl(githubUrl)
                .submittedBy(submittedBy)
                .submittedAt(submittedAt)
                .score(score)
                .feedback(feedback)
                .status(status)
                .members(group.getMembers() != null ? 
                         group.getMembers().stream()
                              .map(m -> m.getStudent().getFullName())
                              .collect(java.util.stream.Collectors.toList()) 
                         : new java.util.ArrayList<>())
                .build();
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
                .learningPathId(activity.getLearningNode() != null && activity.getLearningNode().getLearningPath() != null ? activity.getLearningNode().getLearningPath().getId() : null)
                .title(activity.getTitle())
                .description(activity.getDescription())
                .maxMembers(activity.getMaxMembers())
                .deadline(activity.getDeadline())
                .createdAt(activity.getCreatedAt())
                .status(activity.getStatus())
                .totalGroups(totalGroups)
                .totalStudents(totalStudents)
                .submittedGroups(submittedGroups)
                .build();
    }
}
