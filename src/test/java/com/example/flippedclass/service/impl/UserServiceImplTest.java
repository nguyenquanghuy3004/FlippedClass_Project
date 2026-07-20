package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateUserRequest;
import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LearningSpaceMemberRepository memberRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest();
        request.setEmail("test@gmail.com");
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setRoles(new HashSet<>(Collections.singletonList("STUDENT")));
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleName.STUDENT);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Collections.singletonList(role)));

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@gmail.com");
        savedUser.setRoles(new HashSet<>(Collections.singletonList(role)));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));
        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_RoleNotFoundInSystem() {
        request.setRoles(new HashSet<>(Collections.singletonList("UNKNOWN_ROLE")));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_RoleNotFoundInDb() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>()); // DB returns empty

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));
        assertTrue(exception.getMessage().contains("Roles not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_EmailNull() {
        request.setEmail(null);
        // Assuming validation is handled by DTO annotations at Controller level, 
        // calling service directly might throw NullPointerException in trim()
        assertThrows(NullPointerException.class, () -> userService.create(request));
    }

    @Test
    void testCreateUser_EmailEmptySpaces() {
        request.setEmail("   ");
        // Service trims the email, but fails validation if it's empty in controller. 
        // Since we are unit testing the service, it trim() to "".
        when(userRepository.existsByEmail("")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        
        Role role = new Role();
        role.setName(RoleName.STUDENT);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Collections.singletonList(role)));

        User savedUser = new User();
        savedUser.setRoles(new HashSet<>(Collections.singletonList(role)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertDoesNotThrow(() -> userService.create(request));
    }

    @Test
    void testCreateUser_UsernameNull() {
        request.setUsername(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThrows(NullPointerException.class, () -> userService.create(request));
    }

    @Test
    void testCreateUser_UsernameTrimmed() {
        request.setUsername("   user123   ");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("user123")).thenReturn(false);

        Role role = new Role();
        role.setName(RoleName.STUDENT);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Collections.singletonList(role)));
        User savedUser = new User();
        savedUser.setRoles(new HashSet<>(Collections.singletonList(role)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("user123", captor.getValue().getUsername());
    }

    @Test
    void testCreateUser_MultipleRolesSuccess() {
        request.setRoles(new HashSet<>(Set.of("STUDENT", "MENTOR")));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        Role role1 = new Role(); role1.setName(RoleName.STUDENT);
        Role role2 = new Role(); role2.setName(RoleName.MENTOR);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Set.of(role1, role2)));

        User savedUser = new User();
        savedUser.setRoles(new HashSet<>(Set.of(role1, role2)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse res = userService.create(request);
        assertEquals(2, res.getRoles().size());
    }

    @Test
    void testCreateUser_RolesNull() {
        request.setRoles(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        assertThrows(NullPointerException.class, () -> userService.create(request));
    }

    @Test
    void testCreateUser_MultipleRolesOneMissingInDb() {
        request.setRoles(new HashSet<>(Set.of("STUDENT", "MENTOR")));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        Role role1 = new Role(); role1.setName(RoleName.STUDENT);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Set.of(role1)));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(request));
        assertTrue(exception.getMessage().contains("Roles not found"));
    }

    @Test
    void testCreateUser_FullNameNull() {
        request.setFullName(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleName.STUDENT);
        when(roleRepository.findByNameIn(anySet())).thenReturn(new HashSet<>(Collections.singletonList(role)));

        User savedUser = new User();
        savedUser.setRoles(new HashSet<>(Collections.singletonList(role)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertDoesNotThrow(() -> userService.create(request));
        
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertNull(captor.getValue().getFullName());
    }
}
