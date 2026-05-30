package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LearningNodeRepository extends JpaRepository<LearningNode,Long> {
    List<LearningNode> findByLearningPathId(Long learningPathId);
}
