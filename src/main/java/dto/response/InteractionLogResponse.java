package dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteractionLogResponse {
    private Long id;
    private Long studentId;
    private Long learningPathId;
    private String interactionType;
    private String summary;
    private LocalDateTime occurredAt;
}
