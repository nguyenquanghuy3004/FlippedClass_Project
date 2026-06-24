package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNodeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LearningNodeItemRepository extends JpaRepository<LearningNodeItem,Long> {
    List<LearningNodeItem> findByLearningNodeIdOrderByPosition(Long learningNodeId); // lấy danh sách tài nguyên của 1 bài học

    @Query("SELECT i FROM LearningNodeItem i JOIN i.learningNode n JOIN n.learningPath p JOIN p.learningSpace s " +
           "WHERE s.id IN :spaceIds AND i.itemType IN :itemTypes " +
           "ORDER BY n.createdAt DESC")
    List<LearningNodeItem> findRecentDocumentsBySpaceIds(
            @Param("spaceIds") List<Long> spaceIds, 
            @Param("itemTypes") List<com.example.flippedclass.enums.ItemType> itemTypes, 
            Pageable pageable);
}
