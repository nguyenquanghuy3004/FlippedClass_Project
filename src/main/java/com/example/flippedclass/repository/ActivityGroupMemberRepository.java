package com.example.flippedclass.repository;

import com.example.flippedclass.entity.ActivityGroupMember;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityGroupMemberRepository extends JpaRepository<ActivityGroupMember, Long> {
    
    // Check if user is in any group of a specific activity. Use PESSIMISTIC_WRITE for concurrency control.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM ActivityGroupMember m WHERE m.group.activity.id = :activityId AND m.user.id = :userId")
    boolean existsByActivityIdAndUserIdWithLock(Long activityId, Long userId);

    boolean existsByGroup_Activity_IdAndUser_Id(Long activityId, Long userId);

    Optional<ActivityGroupMember> findByGroup_IdAndUser_Id(Long groupId, Long userId);
}
