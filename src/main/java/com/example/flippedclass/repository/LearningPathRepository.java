package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningPath;
import enums.LearningPathStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByLearningSpaceIdAndStatusOrderByPositionAsc(Long learningSpaceId, LearningPathStatus status);

    Optional<LearningPath> findByIdAndLearningSpaceId(Long id, Long learningSpaceId);

    Optional<LearningPath> findFirstByLearningSpaceIdAndStatusOrderByPositionDesc(Long learningSpaceId, LearningPathStatus status);
}