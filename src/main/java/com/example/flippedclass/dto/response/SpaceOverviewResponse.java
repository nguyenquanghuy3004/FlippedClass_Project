package com.example.flippedclass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceOverviewResponse {
    private int activeStudents;
    private int avgCompletionPercentage;
    private int lessonsPublished;
    private int pendingReviews;
    private List<StrugglingLesson> strugglingLessons;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrugglingLesson {
        private String lessonName;
        private int failRatePercentage;
    }
}
