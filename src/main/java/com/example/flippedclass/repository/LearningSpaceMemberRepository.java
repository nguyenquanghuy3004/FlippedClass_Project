package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.User;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {

    Optional<LearningSpaceMember> findByLearningSpaceIdAndUserUsername(Long learningSpaceId, String username);

    boolean existsByLearningSpaceAndUser(LearningSpace learningSpace, User user);

    boolean existsByUser_IdAndRole(Long userId, com.example.flippedclass.enums.MemberRole role);

    long countByLearningSpaceIdAndRole(Long learningSpaceId, com.example.flippedclass.enums.MemberRole role);

    boolean existsByLearningSpaceIdAndUserId(Long learningSpaceId, Long userId);

    List<LearningSpaceMember> findByUser_IdOrderByJoinedAtDesc(Long userId);
    int countByUser_Id(Long userId);

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    Page<LearningSpaceMember> findByLearningSpaceId(@org.springframework.data.repository.query.Param("spaceId") Long learningSpaceId, Pageable pageable);

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    List<LearningSpaceMember> findByLearningSpaceId(@org.springframework.data.repository.query.Param("spaceId") Long learningSpaceId);

    @Query("SELECT COUNT(m) FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    long countByLearningSpaceId(@org.springframework.data.repository.query.Param("spaceId") Long learningSpaceId);
    Optional<LearningSpaceMember> findByIdAndLearningSpaceId(Long id, Long learningSpaceId);
    Optional<LearningSpaceMember> findByLearningSpace_IdAndUser_Id(Long learningSpaceId, Long userId);
}
