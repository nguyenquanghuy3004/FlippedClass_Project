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


import com.example.flippedclass.repository.LearningSpaceMemberRepository;
import com.example.flippedclass.enums.MemberRole;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LearningSpaceMemberRepository memberRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, LearningSpaceMemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
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
        UserResponse response = toResponse(findUser(id));
        if (memberRepository != null) {
            response.setSupporter(memberRepository.existsByUser_IdAndRole(id, MemberRole.SUPPORTER));
        }
        return response;
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserServiceImpl::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(findUser(id));
    }

    static User findUser(UserRepository repo, Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private User findUser(Long id) {
        return findUser(userRepository, id);
    }

    @Override
    public void updateActiveTime(Long userId, int activeSeconds) {
        User user = findUser(userId);
        if (user.getTotalActiveTime() == null) {
            user.setTotalActiveTime((long) activeSeconds);
        } else {
            user.setTotalActiveTime(user.getTotalActiveTime() + activeSeconds);
        }
        userRepository.save(user);
    }

    static UserResponse toResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
                
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .roles(roleNames)
                .isSupporter(false)
                .createdAt(user.getCreatedAt())
                .build();
    }
}