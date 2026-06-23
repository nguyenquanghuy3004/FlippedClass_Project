package com.example.flippedclass.dto.request.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCreateRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must be less than 100 characters")
    private String groupName;
}
