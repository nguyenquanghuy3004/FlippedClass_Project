package dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private Long learningNodeId;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private Integer durationMinutes;
    private boolean active;
    private LocalDateTime createdAt;
}
