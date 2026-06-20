package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class DeadlinePassedException extends BusinessException {
    public DeadlinePassedException(String message) {
        super("DEADLINE_PASSED", message, HttpStatus.BAD_REQUEST);
    }
}
