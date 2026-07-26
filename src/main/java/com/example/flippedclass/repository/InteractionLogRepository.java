package com.example.flippedclass.repository;

import com.example.flippedclass.entity.InteractionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InteractionLogRepository extends JpaRepository<InteractionLog, Long> {

    List<InteractionLog> findByStudentIdOrderByOccurredAtDesc(Long studentId);

    List<InteractionLog> findByStudentIdAndLearningPathIdOrderByOccurredAtDesc(Long studentId, Long learningPathId);

    List<InteractionLog> findTop20ByStudentIdAndLearningPath_LearningSpace_IdOrderByOccurredAtDesc(Long studentId, Long spaceId);

    @Query("SELECT MAX(il.occurredAt) FROM InteractionLog il " +
           "WHERE il.student.id = :studentId " +
           "AND il.learningPath.learningSpace.id = :spaceId")
    java.time.LocalDateTime findLastInteractionByStudentAndSpace(@Param("studentId") Long studentId, @Param("spaceId") Long spaceId);
}
