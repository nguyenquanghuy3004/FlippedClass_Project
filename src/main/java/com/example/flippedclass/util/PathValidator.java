package com.example.flippedclass.util;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import java.util.regex.Pattern;

public class PathValidator {

    private static final String TITLE_PATTERN = "^[\\p{L}\\d\\s\\-_.,?!()]+$";

    public static void validateCreate(CreateLearningPathRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
    }

    public static void validateUpdate(UpdateLearningPathRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Learning path title is required");
        }
        if (title.length() < 5 || title.length() > 150) {
            throw new IllegalArgumentException("Learning path title must be between 5 and 150 characters");
        }
        if (!Pattern.matches(TITLE_PATTERN, title)) {
            throw new IllegalArgumentException("Learning path title contains invalid characters");
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Description must not exceed 1000 characters");
        }
    }
}
