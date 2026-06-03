package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.*;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

      return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest tokenRequest) {
        try {
            return ResponseEntity.ok(authService.googleLogin(tokenRequest));
        }catch (Exception e){
            return  ResponseEntity.internalServerError().body(new MessageResponse("Verifying google token failed! " + e.getMessage()));
        }
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody CompleteProfileRequest request) {
        return ResponseEntity.ok(authService.completeProfile(request));
    }

    // change password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassWord(@RequestBody ChangePasswordRequest changePass) {

        return ResponseEntity.ok(authService.changePassWord(changePass));
    }

}