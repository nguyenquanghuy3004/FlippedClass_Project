package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeDiscussion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodeDiscussionRepository extends JpaRepository<NodeDiscussion, Long> {

    @Query("SELECT nd FROM NodeDiscussion nd WHERE nd.learningNode.id = :nodeId AND nd.parentDiscussion IS NULL ORDER BY nd.isPinned DESC, nd.createdAt DESC")
    List<NodeDiscussion> findRootDiscussionsByNodeId(@Param("nodeId") Long nodeId);
    
    long countByLearningNodeIdAndParentDiscussionIsNull(Long nodeId);
    
    @Query("SELECT COUNT(nd) FROM NodeDiscussion nd WHERE nd.learningNode.id = :nodeId AND nd.parentDiscussion IS NULL AND nd.status = 'SOLVED'")
    long countSolvedByNodeId(@Param("nodeId") Long nodeId);
}
