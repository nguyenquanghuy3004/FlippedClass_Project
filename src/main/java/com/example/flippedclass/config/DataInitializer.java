package com.example.flippedclass.config;

import com.example.flippedclass.entity.Role;
import com.example.flippedclass.entity.User;
import enums.AuthProvider;
import enums.RoleName;
import com.example.flippedclass.repository.RoleRepository;
import com.example.flippedclass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${flippedclass.app.initialPassword}")
    private String initialPassword;

    @Override
    public void run(String... args) throws Exception {
        // 1. Khởi tạo các Role nếu chưa tồn tại
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        // 2. Khởi tạo duy nhất 1 tài khoản Giảng viên kiêm Quản trị viên (Superuser) nếu chưa có Mentor nào
        // Đây là mô hình chạy cho cá nhân giảng viên tự vận hành (Model B). Giảng viên nắm giữ cả 2 quyền tối cao.
        if (userRepository.countByRolesName(RoleName.MENTOR) == 0) {
            seedSuperUser("giangvien@fpt.edu.vn", "giangvien", "Thay Nguyen Van A", initialPassword);
        }
    }

    /**
     * Phương thức khởi tạo tài khoản Giảng viên kiêm Admin hệ thống (Superuser)
     */
    private void seedSuperUser(String email, String username, String fullName, String password) {
        // Chỉ tạo tài khoản khi email này chưa tồn tại trong cơ sở dữ liệu
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setFullName(fullName);
            // Mã hóa mật khẩu bằng BCrypt trước khi lưu vào Database để đảm bảo an toàn tuyệt đối
            user.setPassword(passwordEncoder.encode(password));
            user.setProvider(AuthProvider.LOCAL);
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+") + "&background=random");

            // Khởi tạo tập hợp chứa các vai trò hệ thống
            Set<Role> roles = new HashSet<>();
            
            // Tìm kiếm Role MENTOR và Role ADMIN từ Database
            Role mentorRole = roleRepository.findByName(RoleName.MENTOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role MENTOR not found."));
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));
                    
            // Gán cả hai vai trò này vào tập hợp roles
            roles.add(mentorRole);
            roles.add(adminRole);

            // Thiết lập danh sách vai trò cho người dùng
            user.setRoles(roles);
            
            // Lưu người dùng (Superuser) vào Database
            userRepository.save(user);
            System.out.println(">>> Da khoi tao thanh cong tai khoan Superuser: " + email);
        }
    }
}
