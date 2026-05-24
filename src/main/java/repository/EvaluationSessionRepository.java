package repository;

import entity.EvaluationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationSessionRepository extends JpaRepository<EvaluationSession, Long> {

    List<EvaluationSession> findByLecturerId(Long lecturerId);
}
