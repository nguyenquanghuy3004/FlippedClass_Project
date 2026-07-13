package com.example.flippedclass.config;

import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.AuthProvider;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private com.example.flippedclass.repository.StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${flippedclass.app.initialPassword}")
    private String initialPassword;

    @Override
    public void run(String... args) throws Exception {

        // Khởi tạo các Role nếu chưa tồn tại
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        if (userRepository.countByRolesName(RoleName.ADMIN) == 0) {
            seedAdmin("admin@system.vn", "admin", "Admin", initialPassword);
        }

        if (userRepository.countByRolesName(RoleName.MENTOR) == 0) {
            seedMentor("giangvien@fpt.edu.vn", "giangvien", "Giangvien", initialPassword);
        }
    }

    // Tạo tài khoản Admin hệ thống
    private void seedAdmin(String email, String username, String fullName, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = buildBaseUser(email, username, fullName, password);

            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));
            user.setRoles(new HashSet<>(Collections.singleton(adminRole)));

            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan ADMIN: " + email);
        }
    }

    // Tạo tài khoản Mentor mẫu
    private void seedMentor(String email, String username, String fullName, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = buildBaseUser(email, username, fullName, password);

            Role mentorRole = roleRepository.findByName(RoleName.MENTOR)
                    .orElseThrow(() -> new RuntimeException("Role MENTOR not found."));
            user.setRoles(new HashSet<>(Collections.singleton(mentorRole)));

            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan MENTOR: " + email);
        }
    }

    // Helper: tạo User cơ bản
    private User buildBaseUser(String email, String username, String fullName, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider(AuthProvider.LOCAL);
        user.setAvatarUrl("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+") + "&background=random");
        return user;
    }
}
