package com.example.flippedclass.controller;

import com.example.flippedclass.dto.*;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.service.EmailService;
import com.example.flippedclass.util.ValidateChangePass;
import com.example.flippedclass.util.ValidateResetPass;
import enums.RoleName;
import enums.AuthProvider;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.security.jwt.JwtUtils;
import com.example.flippedclass.service.UserDetailsImpl;
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
    private ValidateResetPass validateResetPass;


    @Autowired
    private EmailService emailService;

    @Autowired
    ValidateChangePass validate;

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
        ValidateProfile.validateCompleteProfile(request);

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

    // Change password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassWord(@RequestBody ChangePasswordRequest changePass) {
        // Gọi validate dữ liệu thô từ component tự viết
        validate.validatePassWord(changePass);

        // Lấy thông tin tài khoản đang đăng nhập hiện tại từ Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new MessageResponse("Error: Unauthorized!"));
        }
        String username = authentication.getName();

        // Tìm tài khoản trong Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // Xác thực mật khẩu cũ bằng BCrypt Matches
        if (!encoder.matches(changePass.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Error: Incorrect old password!");
        }

        // Kiểm tra tránh đổi mật khẩu mới trùng mật khẩu cũ
        if (encoder.matches(changePass.getNewPassWord(), user.getPassword())) {
            throw new IllegalArgumentException("Error: New password must be different from old password!");
        }

        // Mã hóa mật khẩu mới và lưu lại
        user.setPassword(encoder.encode(changePass.getNewPassWord()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    // 1. API Yêu cầu Khôi phục mật khẩu (Forgot Password)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email must not be blank!"));
        }
        // Tìm User theo Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Email address not found."));
        // Sinh ra Token ngẫu nhiên và đặt thời gian hết hạn là 15 phút sau
        String token = java.util.UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        // Kích hoạt gửi Email HTML chạy ngầm Background dưới nền
        emailService.sendResetPasswordEmail(user.getEmail(), token);
        // Trả về thông báo thành công cho FE lập tức (dưới 0.1 giây)
        return ResponseEntity.ok(new MessageResponse("Reset password link has been sent to your email successfully!"));
    }


    // 2. API Thực hiện Khôi phục mật khẩu (Reset Password)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        validateResetPass.validateResetPassword(request);

        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));
        // Kiểm tra thời gian hết hạn của token
        if (user.getResetPasswordTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Reset token has expired!"));
        }
        // Mã hóa mật khẩu mới và lưu lại
        user.setPassword(encoder.encode(request.getNewPassword()));

        // Xóa trắng token cũ đi để bảo mật
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

}