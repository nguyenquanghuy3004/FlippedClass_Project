package com.example.flippedclass.repository;

import com.example.flippedclass.entity.GradeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeEntryRepository extends JpaRepository<GradeEntry, Long> {

    List<GradeEntry> findBySessionIdAndStudentId(Long sessionId, Long studentId);

    List<GradeEntry> findByStudentId(Long studentId);

    Optional<GradeEntry> findBySessionIdAndStudentIdAndCriterionId(Long sessionId, Long studentId, Long criterionId);

    List<GradeEntry> findBySession_LearningPath_LearningSpace_IdAndStudentId(Long spaceId, Long studentId);
}
