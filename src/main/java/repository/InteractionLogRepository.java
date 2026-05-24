package repository;

import entity.InteractionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionLogRepository extends JpaRepository<InteractionLog, Long> {

    List<InteractionLog> findByStudentIdOrderByOccurredAtDesc(Long studentId);

    List<InteractionLog> findByStudentIdAndLearningPathIdOrderByOccurredAtDesc(Long studentId, Long learningPathId);
}
