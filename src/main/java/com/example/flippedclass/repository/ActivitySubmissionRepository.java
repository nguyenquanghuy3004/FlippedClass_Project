package com.example.flippedclass.repository;

import com.example.flippedclass.entity.ActivitySubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySubmissionRepository extends JpaRepository<ActivitySubmission, Long> {
    Optional<ActivitySubmission> findByGroupId(Long groupId);
    List<ActivitySubmission> findByActivityId(Long activityId);
}
