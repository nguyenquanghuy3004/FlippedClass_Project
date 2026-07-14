package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudyGroupMember;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    @Query("SELECT COUNT(m) > 0 FROM StudyGroupMember m WHERE m.group.activity.id = :activityId AND m.student.id = :studentId")
    boolean existsByActivityIdAndStudentId(@Param("activityId") Long activityId, @Param("studentId") Long studentId);
    
    boolean existsByGroupIdAndStudentId(Long groupId, Long studentId);
    
    Optional<StudyGroupMember> findByGroupIdAndStudentId(Long groupId, Long studentId);

    @Query("SELECT m FROM StudyGroupMember m WHERE m.student.id = :studentId AND m.group.activity.learningSpace.id = :spaceId")
    java.util.List<StudyGroupMember> findByStudentIdAndLearningSpaceId(@Param("studentId") Long studentId, @Param("spaceId") Long spaceId);
}
