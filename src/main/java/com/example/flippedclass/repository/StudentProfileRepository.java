package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudentProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Truy vấn dữ liệu hồ sơ sinh viên
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser_Id(Long userId);
}
