package com.example.flippedclass.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyInGroupException extends BusinessException {
    public UserAlreadyInGroupException() {
        super("ALREADY_IN_GROUP", "User is already in a group for this activity.", HttpStatus.CONFLICT);
    }
}
