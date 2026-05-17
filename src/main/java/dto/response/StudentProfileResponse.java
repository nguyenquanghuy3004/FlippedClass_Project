package dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentProfileResponse {
    private UserResponse student;
    private List<InteractionLogResponse> recentInteractions;
    private List<GradeEntryResponse> gradeHistory;
}
