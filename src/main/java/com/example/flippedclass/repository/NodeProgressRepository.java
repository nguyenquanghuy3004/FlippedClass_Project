package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeProgressRepository extends JpaRepository<NodeProgress, Long> {
    Optional<NodeProgress> findByStudentIdAndLearningNodeId(Long studentId, Long learningNodeId);
}
