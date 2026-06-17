package com.example.flippedclass.dto.response;

import com.example.flippedclass.enums.SummaryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonSummaryResponse {
    private Long id;
    private Long learningNodeId;
    private Long studentId;
    private String studentName;
    private String summaryContent;
    private String lecturerFeedback;
    private SummaryStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
