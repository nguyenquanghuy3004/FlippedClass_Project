package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<LearningNode, Long> {
    List<LearningNode> findByLearningPathIdOrderByDisplayOrderAsc(Long learningPathId);

    boolean existsByLearningPathIdAndDisplayOrder(Long learningPathId, Integer displayOrder);

    boolean existsByLearningPathIdAndDisplayOrderAndIdNot(Long learningPathId, Integer displayOrder, Long id);
}
