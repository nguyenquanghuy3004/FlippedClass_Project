package dto.response;

import entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
}
