package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LearningNodeRepository extends JpaRepository<LearningNode,Long> {

    /*
    @Query("SELECT COUNT(n) FROM LearningNode n WHERE n.learningPath.learningSpace.id = :spaceId AND (n.isOptional = false OR n.isOptional IS NULL) AND (n.status IS NULL OR n.status != 'DELETED')")
    long countTotalNodesBySpaceId(@Param("spaceId") Long spaceId);
    */
    @Query("SELECT COUNT(n) FROM LearningNode n WHERE n.learningPath.learningSpace.id = :spaceId AND (n.isOptional = false OR n.isOptional IS NULL)")
    long countTotalNodesBySpaceId(@Param("spaceId") Long spaceId);

    /*
    @Query("SELECT n FROM LearningNode n WHERE n.learningPath.id = :learningPathId AND (n.status IS NULL OR n.status != 'DELETED')")
    List<LearningNode> findByLearningPathId(@Param("learningPathId") Long learningPathId);
    */
    List<LearningNode> findByLearningPathId(Long learningPathId);

    @Query("SELECT n FROM LearningNode n WHERE " +
            "(:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR n.status = :status)")
    org.springframework.data.domain.Page<LearningNode> findAllForAdmin(@Param("keyword") String keyword,
                                                                       @Param("status") String status,
                                                                       org.springframework.data.domain.Pageable pageable);
}
