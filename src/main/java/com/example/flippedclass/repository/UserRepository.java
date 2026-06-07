package com.example.flippedclass.repository;

import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    long countByRolesName(RoleName name);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE " +
            "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:role IS NULL OR r.name = :role) AND " +
            "(:status IS NULL OR u.status = :status)")

    Page<User> findUsersWithFilters(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("role") RoleName role,
            @org.springframework.data.repository.query.Param("status") com.example.flippedclass.enums.UserStatus status,
            org.springframework.data.domain.Pageable pageable);
}
