package com.example.flippedclass.repository;

import com.example.flippedclass.entity.GroupActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupActivityRepository extends JpaRepository<GroupActivity, Long> {
    List<GroupActivity> findByLearningNodeId(Long learningNodeId);
}
