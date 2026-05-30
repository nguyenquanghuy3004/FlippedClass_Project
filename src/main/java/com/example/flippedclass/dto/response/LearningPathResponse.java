package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.LearningPathStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class LearningPathResponse {
    private Long id;
    private Long learningSpaceId;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private Integer position;
    private LearningPathStatus status;
    private List<LearningNodeResponse> nodes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
