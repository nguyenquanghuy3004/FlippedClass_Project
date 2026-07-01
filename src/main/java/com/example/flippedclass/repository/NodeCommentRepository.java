package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeCommentRepository extends JpaRepository<NodeComment, Long> {
    List<NodeComment> findByLearningNodeIdAndParentCommentIsNullOrderByCreatedAtDesc(Long nodeId);
    List<NodeComment> findByLearningNodeIdOrderByCreatedAtAsc(Long nodeId);
}
