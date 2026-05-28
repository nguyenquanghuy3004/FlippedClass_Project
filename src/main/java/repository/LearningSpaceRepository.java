package repository;

import entity.LearningSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningSpaceRepository extends JpaRepository<LearningSpace, Long> {
}
