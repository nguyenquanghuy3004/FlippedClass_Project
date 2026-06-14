package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LearningNodeRepository extends JpaRepository<LearningNode,Long> {

    @Query("SELECT COUNT(n) FROM LearningNode n WHERE n.learningPath.learningSpace.id = :spaceId")
    long countTotalNodesBySpaceId(@Param("spaceId") Long spaceId);

    List<LearningNode> findByLearningPathId(Long learningPathId);
}
