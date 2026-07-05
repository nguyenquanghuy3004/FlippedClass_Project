package com.example.flippedclass.repository;

import com.example.flippedclass.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    
    List<StudyGroup> findByActivityId(Long activityId);
    
    @Query("SELECT g FROM StudyGroup g WHERE g.activity.id = :activityId AND SIZE(g.members) < g.activity.maxMembers")
    List<StudyGroup> findAvailableGroups(@Param("activityId") Long activityId);
    
    @Query("SELECT g FROM StudyGroup g JOIN g.members m WHERE g.activity.id = :activityId AND m.student.id = :studentId")
    Optional<StudyGroup> findByActivityIdAndStudentId(@Param("activityId") Long activityId, @Param("studentId") Long studentId);
}
