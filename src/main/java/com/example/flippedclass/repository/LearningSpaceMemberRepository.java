package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {
    Optional<LearningSpaceMember> findByLearningSpaceIdAndUserUsername(Long learningSpaceId, String username);
    boolean existsByLearningSpaceAndUser(LearningSpace learningSpace, User user);
    List<LearningSpaceMember> findByUser_IdOrderByJoinedAtDesc(Long userId);
}
