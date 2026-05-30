package com.example.flippedclass.repository;

import com.example.flippedclass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface   UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
