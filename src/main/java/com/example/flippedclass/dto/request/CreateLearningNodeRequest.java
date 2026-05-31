package com.example.flippedclass.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class  CreateLearningNodeRequest {
    private String title;
    private String description;
    private Long learningPathId;
    private Double positionX;
    private Double positionY;
    private String nodeType;
}

