package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.dto.request.CreateUserRequest;

import java.util.List;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    void delete(Long id);
}
