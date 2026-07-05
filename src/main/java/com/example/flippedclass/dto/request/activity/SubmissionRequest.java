package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SubmissionRequest {
    @Pattern(regexp = "^https?://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+/?$", message = "Invalid Github URL format")
    private String githubRepoUrl;
    
    private String note;
}
