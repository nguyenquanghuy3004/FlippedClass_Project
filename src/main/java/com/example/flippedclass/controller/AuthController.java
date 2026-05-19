package com.example.flippedclass.controller;

import com.example.flippedclass.dto.LoginRequest;
import com.example.flippedclass.dto.SignupRequest;
import com.example.flippedclass.dto.JwtResponse;
import com.example.flippedclass.dto.MessageResponse;
import com.example.flippedclass.dto.TokenRequest;
import com.example.flippedclass.dto.CompleteProfileRequest;
import com.example.flippedclass.dto.GoogleJwtResponse;
import com.example.flippedclass.entity.Role;
import enums.RoleName;
import enums.AuthProvider;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.security.jwt.JwtUtils;
import com.example.flippedclass.service.UserDetailsImpl;
import com.example.flippedclass.util.Validate;
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
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${flippedclass.app.googleClientId}")
    private String googleClientId;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Tạo tài khoản mới
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mentor":
                        Role mentorRole = roleRepository.findByName(RoleName.MENTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(mentorRole);
                        break;
                    default:
                        Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(studentRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest tokenRequest) {
        if (tokenRequest == null || tokenRequest.getIdTokenString() == null || tokenRequest.getIdTokenString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Google Token (idTokenString) must not be blank!"));
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenRequest.getIdTokenString());
            if (idToken == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid Google Token!"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            // Tìm hoặc tạo User mới
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(email.split("@")[0]); // username tạm
                user.setFullName(name);
                user.setAvatarUrl(pictureUrl);
                user.setProvider(AuthProvider.GOOGLE);

                // Mặc định cho đăng ký Google là ROLE_STUDENT
                Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                user.setRoles(new HashSet<>(Collections.singletonList(studentRole)));

                user = userRepository.save(user);
            } else {
                // Cập nhật ảnh đại diện nếu có thay đổi
                if (pictureUrl != null && !pictureUrl.equals(user.getAvatarUrl())) {
                    user.setAvatarUrl(pictureUrl);
                    userRepository.save(user);
                }
            }

            // Sinh JWT từ username
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername());

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList());

            // Check xem đã hoàn thành profile (có StudentProfile) chưa
            boolean isProfileComplete = (user.getStudentProfile() != null);

            return ResponseEntity.ok(new GoogleJwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getAvatarUrl(),
                    roles,
                    isProfileComplete
            ));
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Error: Verifying Google Token failed! " + e.getMessage()));
        }
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody CompleteProfileRequest request) {
        Validate.validateCompleteProfile(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new MessageResponse("Error: Unauthorized!"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // Kiểm tra xem đã có profile chưa
        if (user.getStudentProfile() != null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Student Profile is already complete!"));
        }

        // Kiểm tra xem MSSV có bị trùng lặp không
        if (studentProfileRepository.existsByStudentCode(request.getStudentCode())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Student Code (MSSV) is already taken!"));
        }

        // Tạo profile mới
        StudentProfile profile = new StudentProfile();
        profile.setUser(user);
        profile.setStudentCode(request.getStudentCode());
        profile.setClassName(request.getClassName());
        profile.setMajor(request.getMajor());
        profile.setEnrollmentYear(request.getEnrollmentYear());

        studentProfileRepository.save(profile);

        return ResponseEntity.ok(new MessageResponse("Profile completed successfully!"));
    }
}
