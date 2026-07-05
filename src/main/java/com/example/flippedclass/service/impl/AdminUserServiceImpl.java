package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.admin.UserAdminDto;
import com.example.flippedclass.dto.admin.UserDetailDto;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.UserActivityLog;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.enums.UserStatus;
import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserActivityLogRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private  LearningSpaceRepository learningSpaceRepository;


    @Autowired
    private  LearningSpaceMemberRepository learningSpaceMemberRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  UserActivityLogRepository userActivityLogRepository;

    @Override
    public Page<UserAdminDto> getUsers(int page, int size, String keyword, RoleName role, UserStatus status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<User> usersPage;
        if (keyword == null && role == null && status == null) {
            usersPage = userRepository.findAll(pageRequest);
        } else {
            usersPage = userRepository.findUsersWithFilters(
                (keyword != null && !keyword.isBlank()) ? keyword : null, 
                role, status, pageRequest);
        }

        return usersPage.map(this::mapToUserAdminDto);
    }

    @Override
    public UserDetailDto getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        int spacesOwned = learningSpaceRepository.countByOwnerId(userId);
        int spacesJoined = learningSpaceMemberRepository.countByUser_Id(userId);

        Long totalSecondsObj = user.getTotalActiveTime();
        long totalSeconds = totalSecondsObj != null ? totalSecondsObj : 0;
        String formattedTime = "0m";
        if (totalSeconds > 0) {
            if (totalSeconds < 60) {
                formattedTime = "< 1m";
            } else {
                long minutes = totalSeconds / 60;
                long hours = minutes / 60;
                minutes = minutes % 60;
                if (hours > 0) {
                    formattedTime = hours + "h " + minutes + "m";
                } else {
                    formattedTime = minutes + "m";
                }
            }
        }

        return UserDetailDto.builder()
                .user(mapToUserAdminDto(user))
                .spacesOwned(spacesOwned)
                .spacesJoined(spacesJoined)
                .totalActiveTimeFormatted(formattedTime)
                .build();
    }

    @Override
    @Transactional
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.LOCKED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    private UserAdminDto mapToUserAdminDto(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return UserAdminDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roleNames)
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
