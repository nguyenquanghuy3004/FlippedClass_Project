package com.example.flippedclass.repository;

import com.example.flippedclass.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByLearningNodeIdOrderByCreatedAtAsc(Long learningNodeId);
    void deleteByLearningNodeId(Long learningNodeId);
}
