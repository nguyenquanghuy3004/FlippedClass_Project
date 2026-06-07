package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LessonSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonSummaryRepository extends JpaRepository<LessonSummary, Long> {
    
    List<LessonSummary> findByLearningNodeId(Long learningNodeId);
    
    List<LessonSummary> findByStudentId(Long studentId);
    
    Optional<LessonSummary> findByStudentIdAndLearningNodeId(Long studentId, Long learningNodeId);
}
