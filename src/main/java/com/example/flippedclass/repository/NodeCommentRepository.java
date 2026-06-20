package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface NodeCommentRepository extends JpaRepository<NodeComment, Long> {
    List<NodeComment> findByLearningNodeIdAndParentCommentIsNullOrderByCreatedAtDesc(Long nodeId);
    List<NodeComment> findByLearningNodeIdOrderByCreatedAtAsc(Long nodeId);

    @Query("SELECT c FROM NodeComment c WHERE c.parentComment.user.id = :userId AND c.user.id != :userId ORDER BY c.createdAt DESC")
    List<NodeComment> findRepliesToUser(@Param("userId") Long userId);
}
