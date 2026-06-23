package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class IllegalActivityStateException extends BusinessException {
    public IllegalActivityStateException(String message) {
        super("ACTIVITY_INVALID_STATE", message, HttpStatus.BAD_REQUEST);
    }
}
