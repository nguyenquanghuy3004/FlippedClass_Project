package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<LearningNode, Long> {

    List<LearningNode> findByLearningPathIdOrderByDisplayOrderAsc(Long learningPathId);

    boolean existsByLearningPathIdAndDisplayOrder(Long learningPathId, Integer displayOrder);

    boolean existsByLearningPathIdAndDisplayOrderAndIdNot(Long learningPathId, Integer displayOrder, Long id);
}
