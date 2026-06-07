package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNodeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningNodeItemRepository extends JpaRepository<LearningNodeItem,Long> {
    List<LearningNodeItem> findByLearningNodeIdOrderByPosition(Long learningNodeId); // lấy danh sách tài nguyên của 1 bài học
}
