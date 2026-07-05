package com.example.flippedclass.repository;

import com.example.flippedclass.entity.GroupActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupActivityRepository extends JpaRepository<GroupActivity, Long> {
    List<GroupActivity> findByLearningNodeId(Long learningNodeId);

    @org.springframework.data.jpa.repository.Query("SELECT ga FROM GroupActivity ga WHERE ga.learningNode.learningPath.learningSpace.id = :spaceId ORDER BY ga.createdAt DESC")
    List<GroupActivity> findBySpaceId(@org.springframework.data.repository.query.Param("spaceId") Long spaceId);

    void deleteByLearningNodeId(Long learningNodeId);
}
