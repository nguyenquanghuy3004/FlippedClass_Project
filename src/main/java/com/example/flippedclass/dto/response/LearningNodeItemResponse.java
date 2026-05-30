package com.example.flippedclass.dto.response;

import enums.ItemType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LearningNodeItemResponse {
    private Long id;
    private String title;
    private ItemType itemType;
    private String url;
    private String fullUrl;
    private String content;
    private Integer position;
    private Long quizId;
}
