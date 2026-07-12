package com.example.flippedclass.util;

import com.example.flippedclass.dto.request.LoginRequest;
import com.example.flippedclass.dto.request.SignupRequest;
import java.util.regex.Pattern;

public class AuthValidator {

    private static final String USERNAME_PATTERN = "^[a-z0-9_]{5,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,40}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static void validateSignup(SignupRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!Pattern.matches(USERNAME_PATTERN, request.getUsername())) {
            throw new IllegalArgumentException("Username must be 5-20 characters long and contain only lowercase letters, numbers, and underscores.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getEmail().length() > 50 || !Pattern.matches(EMAIL_PATTERN, request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format or exceeds 50 characters.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (!Pattern.matches(PASSWORD_PATTERN, request.getPassword())) {
            throw new IllegalArgumentException("Password must be 8-40 characters, contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character.");
        }

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getFullName().length() < 3 || request.getFullName().length() > 50) {
            throw new IllegalArgumentException("Full name must be between 3 and 50 characters.");
        }
    }

    public static void validateLogin(LoginRequest request) {
        if ((request.getUsername() == null || request.getUsername().isBlank()) && 
            (request.getEmail() == null || request.getEmail().isBlank())) {
            throw new IllegalArgumentException("Username or email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
    }
}
