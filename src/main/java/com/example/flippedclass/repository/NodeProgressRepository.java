package com.example.flippedclass.repository;

import com.example.flippedclass.entity.NodeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface NodeProgressRepository extends JpaRepository<NodeProgress, Long> {
    @Query("SELECT COUNT(np) FROM NodeProgress np " + "WHERE np.student.id = :studentId " +  "AND np.learningNode.learningPath.learningSpace.id = :spaceId " +
            "AND np.status = 'COMPLETED'")
    long countCompletedNodesByStudentAndASpace(@Param("studentId") Long studentId, @Param("spaceId") Long spaceId);
    @Query("SELECT np FROM NodeProgress np " + 
           "JOIN FETCH np.learningNode ln " +
           "WHERE np.student.id = :studentId " + 
           "AND np.learningNode.learningPath.learningSpace.id = :spaceId " +
           "AND np.status = 'COMPLETED' " +
           "ORDER BY np.completedAt DESC")
    List<NodeProgress> findCompletedNodesByStudentAndSpace(@Param("studentId") Long studentId, @Param("spaceId") Long spaceId);

    Optional<NodeProgress> findByStudentIdAndLearningNodeId(Long studentId, Long learningNodeId);

    void deleteByLearningNodeId(Long learningNodeId);
}
