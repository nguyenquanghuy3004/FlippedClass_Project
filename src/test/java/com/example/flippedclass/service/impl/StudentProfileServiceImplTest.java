package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.UpdateProfileRequest;
import com.example.flippedclass.dto.response.StudentDetailResponse;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @InjectMocks
    private StudentProfileServiceImpl studentProfileService;

    private User testUser;
    private StudentProfile testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("student1");
        testUser.setEmail("student1@email.com");

        testProfile = new StudentProfile();
        testProfile.setUser(testUser);
        testProfile.setStudentCode("SV123");
    }

    @Test
    void testUpdateProfile_SuccessAllFields() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("New Name");
        request.setAvatarUrl("http://new-avatar.com");
        request.setPhoneNumber("0987654321");
        request.setBio("New Bio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(studentProfileRepository.save(any(StudentProfile.class))).thenReturn(testProfile);

        StudentDetailResponse response = studentProfileService.updateProfile(1L, request);

        assertEquals("New Name", testUser.getFullName());
        assertEquals("http://new-avatar.com", testUser.getAvatarUrl());
        assertEquals("0987654321", testProfile.getPhoneNumber());
        assertEquals("New Bio", testProfile.getBio());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentProfileService.updateProfile(9999L, request));
        assertEquals("Không tìm thấy User!", ex.getMessage());
    }

    @Test
    void testUpdateProfile_ProfileDoesNotExist() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        studentProfileService.updateProfile(1L, request);

        verify(studentProfileRepository).save(any(StudentProfile.class));
    }

    @Test
    void testUpdateProfile_OnlyFullName() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        studentProfileService.updateProfile(1L, request);

        assertEquals("New Name", testUser.getFullName());
        assertNull(testUser.getAvatarUrl()); // unchanged
        assertNull(testProfile.getPhoneNumber()); // unchanged
    }

    @Test
    void testUpdateProfile_OnlyAvatarUrl() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl("http://avatar");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        studentProfileService.updateProfile(1L, request);

        assertEquals("http://avatar", testUser.getAvatarUrl());
        assertNull(testUser.getFullName());
    }

    @Test
    void testUpdateProfile_OnlyPhoneNumber() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPhoneNumber("0123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        studentProfileService.updateProfile(1L, request);

        assertEquals("0123", testProfile.getPhoneNumber());
    }

    @Test
    void testUpdateProfile_OnlyBio() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("My bio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        studentProfileService.updateProfile(1L, request);

        assertEquals("My bio", testProfile.getBio());
    }

    @Test
    void testUpdateProfile_AllNull() {
        UpdateProfileRequest request = new UpdateProfileRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        studentProfileService.updateProfile(1L, request);

        assertNull(testUser.getFullName());
        assertNull(testUser.getAvatarUrl());
        assertNull(testProfile.getPhoneNumber());
        assertNull(testProfile.getBio());
    }

    @Test
    void testUpdateProfile_FullNameEmptySpaces() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        // This validates if we have interceptor for empty strings, currently code just sets it
        studentProfileService.updateProfile(1L, request);
        assertEquals("   ", testUser.getFullName());
    }

    @Test
    void testUpdateProfile_AvatarUrlInvalid() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl("not-url");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        studentProfileService.updateProfile(1L, request);
        assertEquals("not-url", testUser.getAvatarUrl());
    }

    @Test
    void testUpdateProfile_PhoneNumberInvalid() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPhoneNumber("abc");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        studentProfileService.updateProfile(1L, request);
        assertEquals("abc", testProfile.getPhoneNumber());
    }

    @Test
    void testUpdateProfile_BioVeryLong() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("a".repeat(1000));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        studentProfileService.updateProfile(1L, request);
        assertEquals(1000, testProfile.getBio().length());
    }

    @Test
    void testUpdateProfile_PhoneNumberEmpty() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPhoneNumber("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        studentProfileService.updateProfile(1L, request);
        assertEquals("", testProfile.getPhoneNumber());
    }
}
