package com.example.flippedclass.repository;

import com.example.flippedclass.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
}
