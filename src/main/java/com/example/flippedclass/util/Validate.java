package com.example.flippedclass.util;

import com.example.flippedclass.dto.CompleteProfileRequest;

public class Validate {

    /**
     * Kiểm tra định dạng cơ bản của hồ sơ sinh viên gửi lên.
     * Nếu không hợp lệ sẽ ném ra IllegalArgumentException, 
     * Spring Boot GlobalExceptionHandler sẽ tự động bắt lấy và trả về mã lỗi 400.
     */
    public static void validateCompleteProfile(CompleteProfileRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Error: Request body must not be null!");
        }
        if (request.getStudentCode() == null || request.getStudentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Student Code (MSSV) must not be blank!");
        }
        if (request.getClassName() == null || request.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Class name must not be blank!");
        }
        if (request.getMajor() == null || request.getMajor().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Major must not be blank!");
        }
        if (request.getEnrollmentYear() == null) {
            throw new IllegalArgumentException("Error: Enrollment Year must not be null!");
        }
    }
}
