package service.impl;

import dto.request.CreateUserRequest;
import dto.response.UserResponse;
import entity.User;
import entity.enums.UserRole;
import exception.BusinessException;
import exception.NotFoundException;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }

        User user = User.builder()
                .email(email)
                .fullName(request.getFullName().trim())
                .role(request.getRole())
                .build();
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getById(Long id) {
        return mapToResponse(findUser(id));
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
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    private UserResponse mapToResponse(User user) {
        return toResponse(user);
    }

    static void requireRole(User user, UserRole role) {
        if (user.getRole() != role) {
            throw new BusinessException("User " + user.getId() + " is not a " + role);
        }
    }
}
