package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {
    Optional<LearningSpaceMember> findByLearningSpaceIdAndUserUsername(Long learningSpaceId, String username);
}
