package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.UserResponse;
import java.util.List;
import java.util.Map;

public interface PeerMentoringService {
    
    double calculateGpaForStudent(Long spaceId, Long studentId);

    // Returns a map of classification (e.g., "STRONG", "AVERAGE", "WEAK") to list of students with details
    Map<String, List<Map<String, Object>>> classifyStudents(Long spaceId);

    // Auto matches STRONG students with WEAK students
    void autoMatchPairs(Long spaceId);

    // Promote a student to SUPPORTER role
    void promoteToSupporter(Long spaceId, Long studentId);

    List<UserResponse> getMentees(Long spaceId, Long mentorId);

    void removeAllPairingsForMentor(Long spaceId, Long mentorId);

    void removeAllPairingsForMember(Long spaceId, Long memberId);
}
