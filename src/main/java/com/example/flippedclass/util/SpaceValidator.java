package com.example.flippedclass.util;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.UpdateLearningSpaceRequest;
import java.util.regex.Pattern;

public class SpaceValidator {

    private static final String NAME_PATTERN = "^[\\p{L}\\d\\s\\-_.,?!()]+$";

    public static void validateCreate(CreateLearningSpaceRequest request) {
        validateName(request.getName());
        validateDescription(request.getDescription());
        if (request.getVisibility() == null) {
            throw new IllegalArgumentException("Visibility is required");
        }
    }

    public static void validateUpdate(UpdateLearningSpaceRequest request) {
        validateName(request.getName());
        validateDescription(request.getDescription());
        if (request.getVisibility() == null) {
            throw new IllegalArgumentException("Visibility is required");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Learning space name is required");
        }
        if (name.length() < 5 || name.length() > 100) {
            throw new IllegalArgumentException("Learning space name must be between 5 and 100 characters");
        }
        if (!Pattern.matches(NAME_PATTERN, name)) {
            throw new IllegalArgumentException("Learning space name contains invalid characters");
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters");
        }
    }
}
