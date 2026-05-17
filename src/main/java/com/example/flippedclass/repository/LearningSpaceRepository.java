package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningSpaceRepository extends JpaRepository<LearningSpace, Long> {

    boolean existsByInviteCode(String inviteCode);

    Optional<LearningSpace> findByIdAndIsDeletedFalse(Long id);

    Page<LearningSpace> findByIsDeletedFalse(Pageable pageable);
}
