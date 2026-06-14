package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.*;
import com.example.flippedclass.dto.response.GoogleJwtResponse;
import com.example.flippedclass.dto.response.JwtResponse;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.StudentProfile;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.StudentProfileRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.security.jwt.JwtUtils;
import com.example.flippedclass.service.AuthService;
import com.example.flippedclass.util.ValidateChangePass;
import com.example.flippedclass.util.ValidateProfile;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.example.flippedclass.enums.AuthProvider;
import com.example.flippedclass.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


 private final  AuthenticationManager authenticationManager;

    private final   UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final StudentProfileRepository studentProfileRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    ValidateChangePass validate;

    @Value("${flippedclass.app.googleClientId}")
    private String googleClientId;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest){

        String identifier = loginRequest.getEmail() != null ? loginRequest.getEmail() : loginRequest.getUsername();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }
    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setFullName(signUpRequest.getFullName());
        user.setProvider(AuthProvider.LOCAL);

        //  người dùng đăng ký Local đều là STUDENT
        Set<Role> roles = new HashSet<>();

        Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
        roles.add(studentRole);
        user.setRoles(roles);
        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }



    @Override
    public GoogleJwtResponse googleLogin(TokenRequest tokenRequest) throws Exception {


        if (tokenRequest == null || tokenRequest.getIdToken() == null || tokenRequest.getIdToken().trim().isEmpty()) {

            throw new IllegalArgumentException("Google Token (idTokenString) must not be blank!");
        }


        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(tokenRequest.getIdToken());


        if (idToken == null) {
            throw new IllegalArgumentException("Invalid Google Token!");
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


            //  đăng ký Google là STUDENT
            Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Role is not found."));
            user.setRoles(new HashSet<>(Collections.singletonList(studentRole)));
            user = userRepository.save(user);
        }

        else {
            // Cập nhật ảnh đại diện nếu có thay đổi
            if (pictureUrl != null && !pictureUrl.equals(user.getAvatarUrl())) {
                user.setAvatarUrl(pictureUrl);
                userRepository.save(user);
            }
        }
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
                
        // Sinh JWT từ username
        String jwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername(), roles);
        // Check xem đã hoàn thành profile
        boolean isProfileComplete = (user.getStudentProfile() != null);
        return new GoogleJwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                roles,
                isProfileComplete
        );
    }
    @Override
    public MessageResponse completeProfile(CompleteProfileRequest request) {
        ValidateProfile.validateCompleteProfile(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized!");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));
        // ktra đã có profile chưa
        if (user.getStudentProfile() != null) {
            throw new IllegalArgumentException("Student Profile is already complete!");
        }

        if (studentProfileRepository.existsByStudentCode(request.getStudentCode())) {
            throw new IllegalArgumentException("Student Code (MSSV) is already taken!");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUser(user);
        profile.setStudentCode(request.getStudentCode());
        profile.setClassName(request.getClassName());
        profile.setMajor(request.getMajor());
        profile.setEnrollmentYear(request.getEnrollmentYear());
        studentProfileRepository.save(profile);
        return new MessageResponse("Profile completed successfully!");
    }
    @Override
    public MessageResponse changePassWord(ChangePasswordRequest changePass) {
        // Gọi validate dữ liệu thô từ component tự viết
        validate.validatePassWord(changePass);
        // Lấy thông tin tài khoản đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized!");
        }
        String username = authentication.getName();
        // Tìm tk trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));
        // Xác thực mật khẩu BCrypt Matches
        if (!encoder.matches(changePass.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password!");
        }
        // Check mật khẩu mới tránh trùng với cũ
        if (encoder.matches(changePass.getNewPassWord(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from old password!");
        }
        // Mã hóa và lưu
        user.setPassword(encoder.encode(changePass.getNewPassWord()));
        userRepository.save(user);
        return new MessageResponse("Password changed successfully!");
    }
}
