package com.example.flippedclass.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LearningSpaceDetailDto extends LearningSpaceDto {
    private long memberCount;
    private long learningPathCount;
    private long lessonCount;
}
