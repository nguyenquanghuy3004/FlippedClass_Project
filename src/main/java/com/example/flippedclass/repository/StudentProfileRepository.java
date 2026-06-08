package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudentCode(String studentCode);

    Optional<StudentProfile> findByUserId(Long userId);

    boolean existsByStudentCode(String studentCode);

    Optional<StudentProfile> findByUser_Id(Long userId);
}

