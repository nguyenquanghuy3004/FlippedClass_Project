package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionRequest {

    @NotBlank(message = "Github URL is required")
    @Pattern(regexp = "^https://github\\.com/.+$", message = "Invalid Github URL format")
    private String githubUrl;

    private String note;
}
