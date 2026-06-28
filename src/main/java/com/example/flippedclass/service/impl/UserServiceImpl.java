package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateUserRequest;
import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.UserService;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }

        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username already exists: " + username);
        }

        Set<String> roleNames = request.getRoles().stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        Set<RoleName> roleEnums = roleNames.stream()
                .map(RoleName::valueOf)
                .collect(Collectors.toSet());
        Set<Role> roles = roleRepository.findByNameIn(roleEnums);
        if (roles.size() != roleEnums.size()) {
            Set<String> found = roles.stream().map(r -> r.getName().name()).collect(Collectors.toSet());
            Set<String> missing = roleNames.stream().filter(r -> !found.contains(r)).collect(Collectors.toSet());
            throw new BusinessException("Roles not found: " + missing);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(request.getPassword())
                .fullName(request.getFullName() != null ? request.getFullName().trim() : null)
                .roles(roles)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getById(Long id) {
        return toResponse(findUser(id));
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserServiceImpl::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(findUser(id));
    }

    @Override
    public void updateActiveTime(Long userId, int activeSeconds) {
        if (activeSeconds <= 0 || activeSeconds > 120) {
            return;
        }
        User user = findUser(userId);
        Long current = user.getTotalActiveTime();
        if (current == null) current = 0L;
        user.setTotalActiveTime(current + activeSeconds);
        userRepository.save(user);
    }

    static User findUser(UserRepository repo, Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private User findUser(Long id) {
        return findUser(userRepository, id);
    }

    static UserResponse toResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        Long totalActiveSecs = user.getTotalActiveTime();
        String formattedTime = "0h 0m";
        if (totalActiveSecs != null && totalActiveSecs > 0) {
            long hours = totalActiveSecs / 3600;
            long minutes = (totalActiveSecs % 3600) / 60;
            formattedTime = hours + "h " + minutes + "m";
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .totalActiveTimeFormatted(formattedTime)
                .build();
    }
}