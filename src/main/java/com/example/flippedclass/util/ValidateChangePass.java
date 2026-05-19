package com.example.flippedclass.util;
import com.example.flippedclass.dto.ChangePasswordRequest;
import org.springframework.stereotype.Component;
@Component
public class ValidateChangePass {

    public void validatePassWord(ChangePasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null");
        }


        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Old password must not be blank");
        }
        if (request.getNewPassWord() == null || request.getNewPassWord().trim().isEmpty()) {
            throw new IllegalArgumentException("New password must not be blank!");
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm password must not be blank!");
        }

        if (!request.getNewPassWord().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Error: New password and confirm password do not match!");
        }
    }
}