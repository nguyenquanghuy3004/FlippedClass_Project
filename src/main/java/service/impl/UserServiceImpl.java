package service.impl;

import dto.request.CreateUserRequest;
import dto.response.UserResponse;
import entity.Role;
import entity.User;
import exception.BusinessException;
import exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.RoleRepository;
import repository.UserRepository;
import service.UserService;

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
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            Set<String> found = roles.stream().map(Role::getName).collect(Collectors.toSet());
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

    static User findUser(UserRepository repo, Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private User findUser(Long id) {
        return findUser(userRepository, id);
    }

    static UserResponse toResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }
}