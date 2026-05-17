package dto.response;

import entity.enums.InteractionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteractionLogResponse {
    private Long id;
    private Long studentId;
    private String courseName;
    private InteractionType type;
    private String summary;
    private LocalDateTime occurredAt;
}
