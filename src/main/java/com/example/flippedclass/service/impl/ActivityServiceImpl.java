package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.ActivityCreateRequest;
import com.example.flippedclass.dto.request.activity.ActivityStatusUpdateRequest;
import com.example.flippedclass.dto.request.activity.ActivityUpdateRequest;
import com.example.flippedclass.dto.response.activity.ActivityDetailResponse;
import com.example.flippedclass.dto.response.activity.ActivityResponse;
import com.example.flippedclass.entity.ClassroomActivity;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.IllegalActivityStateException;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ClassroomActivityRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ClassroomActivityRepository activityRepository;
    private final LearningSpaceRepository learningSpaceRepository;

    @Override
    @Transactional
    public ActivityResponse createActivity(Long classroomId, ActivityCreateRequest request) {
        if (request.getMinMembersPerGroup() > request.getMaxMembersPerGroup()) {
            throw new BusinessException("INVALID_CONFIG", "minMembers cannot be greater than maxMembers");
        }

        LearningSpace classroom = learningSpaceRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundException("Classroom not found"));

        ClassroomActivity activity = ClassroomActivity.builder()
                .classroom(classroom)
                .title(request.getTitle())
                .description(request.getDescription())
                .minMembersPerGroup(request.getMinMembersPerGroup())
                .maxMembersPerGroup(request.getMaxMembersPerGroup())
                .allowLateSubmission(request.isAllowLateSubmission())
                .autoGroupEnabled(request.isAutoGroupEnabled())
                .deadline(request.getDeadline())
                .status(ActivityStatus.DRAFT)
                .build();

        activity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityDetailResponse getActivityDetails(Long activityId) {
        ClassroomActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
        return mapToDetailResponse(activity);
    }

    @Override
    @Transactional
    public ActivityResponse updateActivity(Long activityId, ActivityUpdateRequest request) {
        ClassroomActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        if (activity.getStatus() != ActivityStatus.DRAFT && activity.getStatus() != ActivityStatus.OPEN) {
            throw new IllegalActivityStateException("Cannot update activity that is locked or graded");
        }

        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setDeadline(request.getDeadline());

        return mapToResponse(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public ActivityResponse updateActivityStatus(Long activityId, ActivityStatusUpdateRequest request) {
        ClassroomActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        activity.setStatus(request.getStatus());
        return mapToResponse(activityRepository.save(activity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivitiesByClassroom(Long classroomId) {
        return activityRepository.findByClassroom_Id(classroomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ActivityResponse mapToResponse(ClassroomActivity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .classroomId(activity.getClassroom().getId())
                .title(activity.getTitle())
                .status(activity.getStatus())
                .deadline(activity.getDeadline())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    private ActivityDetailResponse mapToDetailResponse(ClassroomActivity activity) {
        return ActivityDetailResponse.builder()
                .id(activity.getId())
                .classroomId(activity.getClassroom().getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .minMembersPerGroup(activity.getMinMembersPerGroup())
                .maxMembersPerGroup(activity.getMaxMembersPerGroup())
                .allowLateSubmission(activity.isAllowLateSubmission())
                .autoGroupEnabled(activity.isAutoGroupEnabled())
                .deadline(activity.getDeadline())
                .status(activity.getStatus())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
