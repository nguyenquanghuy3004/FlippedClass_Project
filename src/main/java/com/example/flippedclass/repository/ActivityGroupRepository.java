package com.example.flippedclass.repository;

import com.example.flippedclass.entity.ActivityGroup;
import com.example.flippedclass.enums.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, Long> {
    
    List<ActivityGroup> findByActivity_Id(Long activityId);
    
    @Query("SELECT g FROM ActivityGroup g WHERE g.activity.id = :activityId AND g.status = :status AND SIZE(g.members) < g.activity.maxMembersPerGroup")
    List<ActivityGroup> findAvailableGroups(Long activityId, GroupStatus status);
    
    Optional<ActivityGroup> findByActivity_IdAndMembers_User_Id(Long activityId, Long userId);
}
