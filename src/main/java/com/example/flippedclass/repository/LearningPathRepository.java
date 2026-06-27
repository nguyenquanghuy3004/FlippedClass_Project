package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.LearningPathStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByLearningSpace_IdOrderByPositionAsc(Long learningSpaceId);

    List<LearningPath> findByLecturer_Id(Long lecturerId);

    List<LearningPath> findByLearningSpaceIdAndStatusOrderByPositionAsc(Long learningSpaceId, LearningPathStatus status);

    Optional<LearningPath> findByIdAndLearningSpaceId(Long id, Long learningSpaceId);

    Optional<LearningPath> findFirstByLearningSpaceIdAndStatusOrderByPositionDesc(Long learningSpaceId, LearningPathStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM LearningPath p WHERE " +
            "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR p.status = :status)")
    org.springframework.data.domain.Page<LearningPath> findAllForAdmin(@org.springframework.data.repository.query.Param("keyword") String keyword,
                                                                       @org.springframework.data.repository.query.Param("status") LearningPathStatus status,
                                                                       org.springframework.data.domain.Pageable pageable);
}