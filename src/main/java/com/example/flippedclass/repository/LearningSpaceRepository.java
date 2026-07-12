package com.example.flippedclass.repository;

import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.enums.VisibilityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSpaceRepository extends JpaRepository<LearningSpace, Long> {

    boolean existsByInviteCode(String inviteCode);


    Optional<LearningSpace> findByInviteCode(String inviteCode);

    Optional<LearningSpace> findByInviteCodeAndStatus(String inviteCode, LearningSpaceStatus status);

    Optional<LearningSpace> findByInviteCodeIgnoreCaseAndStatus(String inviteCode, LearningSpaceStatus status);

    Optional<LearningSpace> findByIdAndStatus(Long id, LearningSpaceStatus status);

    @EntityGraph(attributePaths = {"owner"})
    List<LearningSpace> findByOwnerIdAndStatus(Long ownerId, LearningSpaceStatus status);

    @EntityGraph(attributePaths = {"owner"})
    List<LearningSpace> findByOwnerId(Long ownerId);

    Page<LearningSpace> findByStatus(LearningSpaceStatus status, Pageable pageable);
    
    int countByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"owner"})
    List<LearningSpace> findByVisibilityAndStatus(VisibilityType visibility, LearningSpaceStatus status);

    @Query("SELECT ls FROM LearningSpace ls WHERE " + "(:keyword IS NULL OR LOWER(ls.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ls.owner.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR ls.status = :status)")
    Page<LearningSpace> searchAndFilterSpaces(@Param("keyword") String keyword, @Param("status") LearningSpaceStatus status, Pageable pageable);
}
