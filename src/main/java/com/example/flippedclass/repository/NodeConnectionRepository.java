package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeConnectionRepository extends JpaRepository<NodeConnection, Long> {

    List<NodeConnection> findByLearningPathId(Long learningPathId);

    boolean existsByLearningPathIdAndSourceNodeIdAndTargetNodeId(
            Long learningPathId,
            Long sourceNodeId,
            Long targetNodeId
    );
}
