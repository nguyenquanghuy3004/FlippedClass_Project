package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.*;
import com.example.flippedclass.dto.response.GoogleJwtResponse;
import com.example.flippedclass.dto.response.JwtResponse;
import com.example.flippedclass.dto.response.MessageResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    MessageResponse registerUser(SignupRequest signupRequest);
    GoogleJwtResponse googleLogin(TokenRequest tokenRequest) throws Exception;
    MessageResponse completeProfile(CompleteProfileRequest completeProfileRequest);
//    MessageResponse changePassWord(ChangePasswordRequest changePass);




}
