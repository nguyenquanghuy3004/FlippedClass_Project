package com.example.flippedclass.util;

import com.example.flippedclass.dto.req.ResetPasswordRequest;
import org.springframework.stereotype.Component;

@Component // Kích hoạt Spring Bean để có thể @Autowired
public class ValidateResetPass {

    public void validateResetPassword(ResetPasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null!");
        }

        // 1. Kiểm tra Token
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Token must not be blank!");
        }

        // 2. Kiểm tra Mật khẩu mới
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password must not be blank!");
        }

        // 3. Kiểm tra Xác nhận mật khẩu mới có khớp nhau không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match!");
        }
    }
}
