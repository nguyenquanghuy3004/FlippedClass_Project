package com.example.flippedclass.repository;

import com.example.flippedclass.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByLearningNode_IdAndActiveTrue(Long learningNodeId);
}
