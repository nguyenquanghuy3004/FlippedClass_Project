package dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long lecturerId;
    private String lecturerName;
    private Integer durationMinutes;
    private boolean active;
    private LocalDateTime createdAt;
}
