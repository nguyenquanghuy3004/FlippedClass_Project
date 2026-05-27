package com.example.flippedclass.dto.request;


import enums.VisibilityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLearningSpaceRequest {
    private String name;

    private String description;

    private VisibilityType visibility;
}
