package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);

    Optional<StudentProfile> findByStudentCode(String studentCode);
    boolean existsByStudentCode(String studentCode);
    Optional<StudentProfile> findByUser_Id(Long userId);
}
