package com.example.flippedclass.util;

import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.request.NodeRequest;

public class NodeValidator {

    public static void validateCreate(CreateLearningNodeRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
        validateContent(request.getContent());
        if (request.getNodeType() == null || request.getNodeType().isBlank()) {
            throw new IllegalArgumentException("Node type is required");
        }
    }

    public static void validateUpdate(NodeRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
        validateContent(request.getContent());
        if (request.getNodeType() == null) {
            throw new IllegalArgumentException("Node type is required");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Node title is required");
        }
        if (title.length() < 5 || title.length() > 150) {
            throw new IllegalArgumentException("Node title must be between 5 and 150 characters");
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Description must not exceed 1000 characters");
        }
    }

    private static void validateContent(String content) {
        if (content != null && content.length() > 5000) {
            throw new IllegalArgumentException("Content must not exceed 5000 characters");
        }
    }
}
