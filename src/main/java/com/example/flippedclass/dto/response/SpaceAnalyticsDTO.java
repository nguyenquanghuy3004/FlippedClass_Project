package com.example.flippedclass.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SpaceAnalyticsDTO {
    private int totalStudents;
    private int averageProgress;
    private int activeStudents;
    private int supporterCandidates;
    private List<StudentAnalyticsDTO> students;
}
