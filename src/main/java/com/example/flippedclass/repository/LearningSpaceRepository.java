package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import org.springframework.data.jpa.repository.JpaRepository;

// Truy vấn dữ liệu Learning Space
public interface LearningSpaceRepository extends JpaRepository<LearningSpace, Long> {
}
