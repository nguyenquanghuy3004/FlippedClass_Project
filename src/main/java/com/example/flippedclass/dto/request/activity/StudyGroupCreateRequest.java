package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudyGroupCreateRequest {
    @NotBlank
    @Size(max = 100)
    private String groupName;
}
