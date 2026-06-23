package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class GroupFullException extends BusinessException {
    public GroupFullException() {
        super("GROUP_FULL", "The group has reached its maximum capacity.", HttpStatus.CONFLICT);
    }
}
