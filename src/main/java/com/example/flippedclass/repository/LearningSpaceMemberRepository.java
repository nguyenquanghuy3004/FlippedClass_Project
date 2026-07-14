package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.user.username = :username AND m.status != 'INACTIVE'")
    Optional<LearningSpaceMember> findByLearningSpaceIdAndUserUsername(@Param("spaceId") Long spaceId, @Param("username") String username);

    @Query("SELECT COUNT(m) > 0 FROM LearningSpaceMember m WHERE m.learningSpace = :space AND m.user = :user AND m.status != 'INACTIVE'")
    boolean existsByLearningSpaceAndUser(@Param("space") LearningSpace space, @Param("user") User user);

    @Query("SELECT COUNT(m) > 0 FROM LearningSpaceMember m WHERE m.user.id = :userId AND m.role = :role AND m.status != 'INACTIVE'")
    boolean existsByUser_IdAndRole(@Param("userId") Long userId, @Param("role") MemberRole role);

    @Query("SELECT COUNT(m) FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.role = :role AND m.status != 'INACTIVE'")
    long countByLearningSpaceIdAndRole(@Param("spaceId") Long spaceId, @Param("role") MemberRole role);

    @Query("SELECT COUNT(m) > 0 FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.user.id = :userId AND m.status != 'INACTIVE'")
    boolean existsByLearningSpaceIdAndUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.user.id = :userId AND m.status != 'INACTIVE' ORDER BY m.joinedAt DESC")
    List<LearningSpaceMember> findByUser_IdOrderByJoinedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(m) FROM LearningSpaceMember m WHERE m.user.id = :userId AND m.status != 'INACTIVE'")
    int countByUser_Id(@Param("userId") Long userId);

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    Page<LearningSpaceMember> findByLearningSpaceId(@Param("spaceId") Long spaceId, Pageable pageable);

    @Query("SELECT m FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    List<LearningSpaceMember> findByLearningSpaceId(@Param("spaceId") Long spaceId);

    @Query("SELECT COUNT(m) FROM LearningSpaceMember m WHERE m.learningSpace.id = :spaceId AND m.status != 'INACTIVE'")
    long countByLearningSpaceId(@Param("spaceId") Long spaceId);
    
    Optional<LearningSpaceMember> findByIdAndLearningSpaceId(Long id, Long learningSpaceId);
    
    Optional<LearningSpaceMember> findByLearningSpace_IdAndUser_Id(Long learningSpaceId, Long userId);

    @Query("SELECT u FROM User u JOIN LearningSpaceMember lsm ON u.id = lsm.user.id " +
           "WHERE lsm.learningSpace.id = :spaceId AND lsm.role = 'MEMBER' AND lsm.status != 'INACTIVE' " +
           "AND u.id NOT IN (SELECT sgm.student.id FROM StudyGroupMember sgm WHERE sgm.group.activity.id = :activityId) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findAvailableStudentsForActivity(@Param("spaceId") Long spaceId, @Param("activityId") Long activityId, @Param("keyword") String keyword);
}
