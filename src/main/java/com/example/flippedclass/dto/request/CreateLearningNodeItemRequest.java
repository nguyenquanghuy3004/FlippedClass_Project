package com.example.flippedclass.dto.request;

import com.example.flippedclass.enums.ItemType;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class CreateLearningNodeItemRequest {
    private String title;
    private ItemType itemType;
    private String url;
    private String content;
    private Long learningNodeId;
    private Long quizId; // Có thể truyền NULL
}