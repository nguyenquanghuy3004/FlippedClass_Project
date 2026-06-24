package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private String errorCode;
    private HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BAD_REQUEST";
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
