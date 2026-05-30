package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningNodeRepository extends JpaRepository<LearningNode, Long> {
}
