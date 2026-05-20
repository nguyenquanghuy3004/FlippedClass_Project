package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import enums.LearningSpaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningSpaceRepository extends JpaRepository<LearningSpace, Long> {

    boolean existsByInviteCode(String inviteCode);

    Optional<LearningSpace> findByInviteCode(String inviteCode);

    Optional<LearningSpace> findByInviteCodeAndStatus(String inviteCode, LearningSpaceStatus status);

    Optional<LearningSpace> findByIdAndStatus(Long id, LearningSpaceStatus status);

    Page<LearningSpace> findByStatus(LearningSpaceStatus status, Pageable pageable);
}
