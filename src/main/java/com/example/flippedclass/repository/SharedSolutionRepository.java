package com.example.flippedclass.repository;

import com.example.flippedclass.entity.SharedSolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedSolutionRepository extends JpaRepository<SharedSolution, Long> {
    // Tìm các solution chia sẻ theo nodeId, sắp xếp theo upvote giảm dần và ngày tạo
    List<SharedSolution> findByLearningNodeIdOrderByUpvotesDescCreatedAtDesc(Long nodeId);
}
