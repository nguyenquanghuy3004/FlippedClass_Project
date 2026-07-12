package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearningNodeRequest {
    private String title;
    private String description;
    private Long learningPathId;
    private String nodeType;
    private String content;
    private String starterCode;
    private String solutionCode;
    private Long prerequisiteNodeId;
}

