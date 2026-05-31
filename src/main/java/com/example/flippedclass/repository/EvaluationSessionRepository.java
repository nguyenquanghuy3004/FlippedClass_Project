package com.example.flippedclass.repository;

import com.example.flippedclass.entity.EvaluationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationSessionRepository extends JpaRepository<EvaluationSession, Long> {

    List<EvaluationSession> findByLecturerId(Long lecturerId);
}
