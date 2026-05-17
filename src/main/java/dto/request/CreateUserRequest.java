package dto.request;

import entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Size(max = 100, message = "email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "fullName is required")
    @Size(min = 2, max = 100, message = "fullName must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "role is required")
    private UserRole role;
}
