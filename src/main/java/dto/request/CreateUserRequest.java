package dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 100, message = "username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @Size(max = 255, message = "password must not exceed 255 characters")
    private String password;

    @Size(min = 2, max = 255, message = "fullName must be between 2 and 255 characters")
    private String fullName;

    @NotEmpty(message = "at least one role is required")
    private Set<String> roles;
}
