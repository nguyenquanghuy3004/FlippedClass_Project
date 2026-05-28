package com.example.flippedclass.repository;

import com.example.flippedclass.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByLearningNode_IdOrderByCreatedAtDesc(Long learningNodeId);
}
