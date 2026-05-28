package repository;

import entity.LearningNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningNodeRepository extends JpaRepository<LearningNode, Long> {
}
