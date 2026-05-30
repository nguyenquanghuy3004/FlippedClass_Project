package com.example.flippedclass.repository;

import com.example.flippedclass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Truy vấn dữ liệu người dùng
public interface UserRepository extends JpaRepository<User, Long> {
}
