package com.example.flippedclass.repository;

import com.example.flippedclass.entity.UserActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findTop50ByUser_IdOrderByCreatedAtDesc(Long userId);
    List<UserActivityLog> findTop50ByOrderByCreatedAtDesc();

    @Query("SELECT u FROM UserActivityLog u WHERE " +
           "(:keyword IS NULL OR LOWER(u.actionType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.user.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(CAST(:startDate AS timestamp) IS NULL OR u.createdAt >= :startDate) AND " +
           "(CAST(:endDate AS timestamp) IS NULL OR u.createdAt <= :endDate) " +
           "ORDER BY u.createdAt DESC")
    org.springframework.data.domain.Page<UserActivityLog> findGlobalActivitiesWithFilter(
            @Param("keyword") String keyword, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
}
