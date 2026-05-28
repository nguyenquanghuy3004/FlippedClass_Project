package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpaceMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {
    List<LearningSpaceMember> findByUser_IdOrderByJoinedAtDesc(Long userId);
}
