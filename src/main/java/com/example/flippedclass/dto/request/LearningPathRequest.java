package com.example.flippedclass.dto.request;

import com.example.flippedclass.enums.LearningPathStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LearningPathRequest {
    private Long learningSpaceId;
    private Long lecturerId;

    @NotBlank(message = "Learning path title is required")
    @Size(max = 255, message = "Learning path title must not exceed 255 characters")
    private String title;

    private String description;
    private LearningPathStatus status;
    private Integer position;
}
