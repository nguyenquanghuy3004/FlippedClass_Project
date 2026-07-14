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
import org.springframework.web.multipart.MultipartFile;
import com.example.flippedclass.dto.admin.AdminUserCreateDto;
import org.apache.poi.ss.usermodel.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
        List<com.example.flippedclass.entity.LearningSpaceMember> members = learningSpaceMemberRepository.findByUser_IdOrderByJoinedAtDesc(userId);
        int spacesJoined = members.size();

        List<com.example.flippedclass.dto.admin.JoinedSpaceDto> joinedSpaces = members.stream().map(m -> 
            com.example.flippedclass.dto.admin.JoinedSpaceDto.builder()
                .id(m.getLearningSpace().getId())
                .name(m.getLearningSpace().getName())
                .role(m.getRole().name())
                .joinedAt(m.getJoinedAt())
                .build()
        ).collect(Collectors.toList());

        return UserDetailDto.builder()
                .user(mapToUserAdminDto(user))
                .spacesOwned(spacesOwned)
                .spacesJoined(spacesJoined)

                .joinedSpaces(joinedSpaces)
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

    @Override
    @Transactional
    public UserAdminDto createUser(AdminUserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        Set<Role> roles = new HashSet<>();
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            for (String roleNameStr : dto.getRoles()) {
                RoleName roleName = RoleName.valueOf(roleNameStr);
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleNameStr));
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return mapToUserAdminDto(savedUser);
    }

    @Override
    @Transactional
    public Map<String, Object> importUsersFromExcel(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Role defaultRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell fullNameCell = row.getCell(0);
                Cell usernameCell = row.getCell(1);
                Cell emailCell = row.getCell(2);

                if (usernameCell == null || emailCell == null) {
                    errors.add("Row " + (i + 1) + ": Username and Email are required");
                    continue;
                }

                org.apache.poi.ss.usermodel.DataFormatter formatter = new org.apache.poi.ss.usermodel.DataFormatter();
                String username = formatter.formatCellValue(usernameCell).trim();
                String email = formatter.formatCellValue(emailCell).trim();
                String fullName = formatter.formatCellValue(fullNameCell).trim();

                if (userRepository.existsByUsername(username)) {
                    errors.add("Row " + (i + 1) + ": Username " + username + " already exists");
                    continue;
                }
                if (userRepository.existsByEmail(email)) {
                    errors.add("Row " + (i + 1) + ": Email " + email + " already exists");
                    continue;
                }

                User user = new User();
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                // Default password for imported users
                user.setPassword(passwordEncoder.encode("123456"));
                user.setStatus(UserStatus.ACTIVE);
                Set<Role> roles = new HashSet<>();
                roles.add(defaultRole);
                user.setRoles(roles);

                userRepository.save(user);
                successCount++;
            }
        } catch (Exception e) {
            errors.add("Error processing file: " + e.getMessage());
        }

        result.put("successCount", successCount);
        result.put("errors", errors);
        return result;
    }
}
