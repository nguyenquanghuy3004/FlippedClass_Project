package repository;

import entity.LearningSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {
}
