package com.example.flippedclass.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
public class LearningNodeItemRequest {
    private String title;
    private String itemType;
    private String url;
}
