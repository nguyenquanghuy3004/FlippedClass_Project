package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TestResultResponse {
    private boolean allPassed;
    private int totalScore;
    private int maxScore;
    private int passedCount;
    private int totalCases;
    private List<TestCaseResult> results;

    @Data
    @Builder
    public static class TestCaseResult {
        private Long testCaseId;
        private boolean passed;
        private boolean hidden;
        private String inputData; // Only if not hidden
        private String expectedOutput; // Only if not hidden
        private String actualOutput; // Only if not hidden
        private int points;
    }
}
