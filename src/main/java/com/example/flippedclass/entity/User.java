package com.example.flippedclass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email") 
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true) // Hỗ trợ tài khoản Google không có mật khẩu
    private String password;

    private String fullName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private enums.AuthProvider provider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentProfile studentProfile;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles", 
            joinColumns = @JoinColumn(name = "user_id"), 
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Mã token để khôi phục mật khẩu
    private String resetPasswordToken;

    // Thời gian hết hạn của token khôi phục (15 phút sau khi tạo)
    private LocalDateTime resetPasswordTokenExpiry;
}
