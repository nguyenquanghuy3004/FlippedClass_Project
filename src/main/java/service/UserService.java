package service;

import dto.response.UserResponse;
import dto.request.CreateUserRequest;

import java.util.List;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    void delete(Long id);
}
