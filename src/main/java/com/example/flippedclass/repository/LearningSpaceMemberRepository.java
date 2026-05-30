package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningSpaceMemberRepository extends JpaRepository<LearningSpaceMember, Long> {
}
