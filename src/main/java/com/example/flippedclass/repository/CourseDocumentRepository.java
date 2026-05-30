package com.example.flippedclass.repository;

import java.util.List;

import com.example.flippedclass.entity.CourseDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseDocumentRepository extends JpaRepository<CourseDocument, Long> {
    List<CourseDocument> findByLearningPathId(Long learningPathId);
}
