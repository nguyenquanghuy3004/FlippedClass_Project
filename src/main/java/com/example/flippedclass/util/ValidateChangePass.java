package com.example.flippedclass.util;
import com.example.flippedclass.dto.ChangePasswordRequest;

public class ValidateChangePass {
    public void validatePassWord(ChangePasswordRequest request){
        if(request == null){
            throw new IllegalArgumentException("Requert body must not be null");
        }
        if(request.getOldPassword().trim().isEmpty() || request.getOldPassword() == null){
            throw new IllegalArgumentException("Old password must not be blank");
        }
        if(request.getNewPassWord().trim().isEmpty() || request.getNewPassWord() == null){
            throw new IllegalArgumentException("New password must not be blank!");
        }
        if(request.getConfirmPassword().trim().isEmpty() || request.getConfirmPassword() == null){
            throw new IllegalArgumentException("Confirm password must not be blank!");
        }

        if(!request.getNewPassWord().equals(request.getNewPassWord())){
            throw new IllegalArgumentException("Error: New password and confirm password do not match!");
        }
    }
}
