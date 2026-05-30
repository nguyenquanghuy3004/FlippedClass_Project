package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningPath;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Truy vấn dữ liệu Learning Path
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    List<LearningPath> findByLearningSpace_IdOrderByPositionAsc(Long learningSpaceId);
}
