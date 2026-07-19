package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.activity.SubmissionRequest;
import com.example.flippedclass.entity.ActivitySubmission;
import com.example.flippedclass.entity.GroupActivity;
import com.example.flippedclass.entity.StudyGroup;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.GroupStatus;
import com.example.flippedclass.enums.SubmissionStatus;
import com.example.flippedclass.exception.NotFoundException;
import com.example.flippedclass.repository.ActivitySubmissionRepository;
import com.example.flippedclass.repository.StudyGroupRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.validation.GroupActivityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private StudyGroupRepository groupRepository;
    @Mock
    private ActivitySubmissionRepository submissionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupActivityValidator validator;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    private StudyGroup group;
    private User user;
    private GroupActivity activity;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        activity = new GroupActivity();
        activity.setId(10L);

        group = new StudyGroup();
        group.setId(100L);
        group.setActivity(activity);
    }

    // --- submitWork tests (13 cases) ---
    @Test
    void testSubmitWork_SuccessFirstTime() {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubRepoUrl("https://github.com/abc/repo");
        request.setNote("We are done");

        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);

        verify(validator).validateLeaderRole(1L, group);
        verify(validator).validateActivityOpen(activity);
        verify(validator).validateDeadline(activity);
        verify(submissionRepository).save(any(ActivitySubmission.class));
        verify(groupRepository).save(group);
        assertEquals(GroupStatus.SUBMITTED, group.getStatus());
        assertNotNull(group.getSubmission());
        assertEquals("https://github.com/abc/repo", group.getSubmission().getGithubRepoUrl());
    }

    @Test
    void testSubmitWork_SuccessOverrideExisting() {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubRepoUrl("https://github.com/new/repo");
        request.setNote("New note");

        ActivitySubmission existingSub = new ActivitySubmission();
        existingSub.setId(50L);
        existingSub.setGithubRepoUrl("old");
        existingSub.setNote("old");
        group.setSubmission(existingSub);

        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        
        submissionService.submitWork(100L, 1L, request);

        verify(submissionRepository).save(existingSub);
        assertEquals("https://github.com/new/repo", existingSub.getGithubRepoUrl());
        assertEquals("New note", existingSub.getNote());
        assertEquals(SubmissionStatus.SUBMITTED, existingSub.getStatus());
    }

    @Test
    void testSubmitWork_GroupNotFound() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> submissionService.submitWork(999L, 1L, request));
        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void testSubmitWork_UserNotLeader() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        doThrow(new RuntimeException("Not leader")).when(validator).validateLeaderRole(1L, group);

        assertThrows(RuntimeException.class, () -> submissionService.submitWork(100L, 1L, request));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void testSubmitWork_ActivityNotOpen() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        doThrow(new RuntimeException("Activity not open")).when(validator).validateActivityOpen(activity);

        assertThrows(RuntimeException.class, () -> submissionService.submitWork(100L, 1L, request));
    }

    @Test
    void testSubmitWork_DeadlinePassed() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        doThrow(new RuntimeException("Deadline passed")).when(validator).validateDeadline(activity);

        assertThrows(RuntimeException.class, () -> submissionService.submitWork(100L, 1L, request));
    }

    @Test
    void testSubmitWork_UserNotFoundInDb() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> submissionService.submitWork(100L, 1L, request));
    }

    @Test
    void testSubmitWork_GithubUrlEmpty() {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubRepoUrl("");
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);
        assertEquals("", group.getSubmission().getGithubRepoUrl());
    }

    @Test
    void testSubmitWork_GithubUrlNull() {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubRepoUrl(null);
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);
        assertNull(group.getSubmission().getGithubRepoUrl());
    }

    @Test
    void testSubmitWork_NoteEmpty() {
        SubmissionRequest request = new SubmissionRequest();
        request.setNote("");
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);
        assertEquals("", group.getSubmission().getNote());
    }

    @Test
    void testSubmitWork_NoteNull() {
        SubmissionRequest request = new SubmissionRequest();
        request.setNote(null);
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);
        assertNull(group.getSubmission().getNote());
    }

    @Test
    void testSubmitWork_VerifySubmittedAt() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        LocalDateTime before = LocalDateTime.now();
        submissionService.submitWork(100L, 1L, request);
        LocalDateTime after = LocalDateTime.now();

        LocalDateTime submittedAt = group.getSubmission().getSubmittedAt();
        assertTrue(!submittedAt.isBefore(before) && !submittedAt.isAfter(after));
    }

    @Test
    void testSubmitWork_VerifyStatus() {
        SubmissionRequest request = new SubmissionRequest();
        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        submissionService.submitWork(100L, 1L, request);
        assertEquals(SubmissionStatus.SUBMITTED, group.getSubmission().getStatus());
    }
}
