package com.example.flippedclass.dto.req;


import enums.VisibilityType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLearningSpaceRequest {
    private String name;

    private String description;

    private VisibilityType visibility;
}
