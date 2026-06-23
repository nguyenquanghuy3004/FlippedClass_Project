package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedSubmissionException extends BusinessException {
    public UnauthorizedSubmissionException(String message) {
        super("UNAUTHORIZED_SUBMISSION", message, HttpStatus.FORBIDDEN);
    }
}
