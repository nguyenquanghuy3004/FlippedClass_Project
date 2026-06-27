package com.example.flippedclass.repository;

import com.example.flippedclass.entity.PeerPairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeerPairingRepository extends JpaRepository<PeerPairing, Long> {
    List<PeerPairing> findByLearningSpace_Id(Long learningSpaceId);
    List<PeerPairing> findByLearningSpace_IdAndMentor_Id(Long learningSpaceId, Long mentorId);
    List<PeerPairing> findByLearningSpace_IdAndMentee_Id(Long learningSpaceId, Long menteeId);
    Optional<PeerPairing> findByLearningSpace_IdAndMentee_IdAndMentor_Id(Long learningSpaceId, Long menteeId, Long mentorId);
    long countByLearningSpace_IdAndMentor_IdAndStatus(Long learningSpaceId, Long mentorId, com.example.flippedclass.enums.PeerPairingStatus status);
    List<PeerPairing> findByMentor_Id(Long mentorId);
}
