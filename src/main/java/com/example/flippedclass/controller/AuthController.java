package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.*;
import com.example.flippedclass.dto.response.GoogleJwtResponse;
import com.example.flippedclass.dto.response.JwtResponse;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.service.AuthService;
import com.example.flippedclass.util.ValidateChangePass;
import enums.RoleName;
import enums.AuthProvider;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.security.jwt.JwtUtils;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import com.example.flippedclass.util.ValidateProfile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

//    @Value("${flippedclass.app.googleClientId}")
//    private String googleClientId;

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