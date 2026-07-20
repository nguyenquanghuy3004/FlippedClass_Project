package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.CompletedNodeResponse;
import com.example.flippedclass.dto.response.SpaceAnalyticsDTO;
import com.example.flippedclass.dto.response.StudentAnalyticsDTO;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.entity.LearningSpaceMember;
import com.example.flippedclass.entity.NodeProgress;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.enums.MemberStatus;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.service.LearningAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningAnalyticsServiceImpl implements LearningAnalyticsService {

    private final LearningSpaceRepository learningSpaceRepository;
    private final LearningSpaceMemberRepository learningSpaceMemberRepository;
    private final LearningNodeRepository learningNodeRepository;
    private final NodeProgressRepository nodeProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final InteractionLogRepository interactionLogRepository;


    @Override
    public SpaceAnalyticsDTO getSpaceAnalytics(Long spaceId) {
        LearningSpace space = learningSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Learning Space not found"));

        long totalNodes = learningNodeRepository.countTotalNodesBySpaceId(spaceId);

        List<LearningSpaceMember> members = learningSpaceMemberRepository.findByLearningSpaceId(spaceId);
        
        List<StudentAnalyticsDTO> studentAnalyticsList = new ArrayList<>();
        int totalStudents = 0;
        int sumProgress = 0;
        int activeStudents = 0;
        int supporterCandidates = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Map<Long, Long> completedNodesMap = nodeProgressRepository.countCompletedNodesGroupedByStudent(spaceId)
                .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).longValue()));

        Map<Long, Double> quizAvgMap = quizAttemptRepository.findAverageScoreGroupedByStudent(spaceId)
                .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> row[1] != null ? ((Number) row[1]).doubleValue() : 0.0));

        Map<Long, LocalDateTime> lastActiveMap = interactionLogRepository.findLastInteractionGroupedByStudent(spaceId)
                .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (LocalDateTime) row[1]));

        for (LearningSpaceMember member : members) {
            if (member.getRole() == MemberRole.OWNER || member.getStatus() != MemberStatus.ACTIVE) {
                continue;
            }

            Long studentId = member.getUser().getId();
            totalStudents++;

            long completedNodes = completedNodesMap.getOrDefault(studentId, 0L);
            int progressPercentage = totalNodes > 0 ? (int) ((completedNodes * 100) / totalNodes) : 0;
            sumProgress += progressPercentage;

            double quizAvg = quizAvgMap.getOrDefault(studentId, 0.0);
            quizAvg = BigDecimal.valueOf(quizAvg).setScale(1, RoundingMode.HALF_UP).doubleValue();

            LocalDateTime lastActiveTime = lastActiveMap.get(studentId);
            String lastActiveStr = "N/A";
            boolean isCandidate = progressPercentage >= 80 && quizAvg >= 8.0;

            if (lastActiveTime != null) {
                lastActiveStr = lastActiveTime.format(formatter);
                long daysSinceActive = ChronoUnit.DAYS.between(lastActiveTime, LocalDateTime.now());
                if (daysSinceActive <= 7) {
                    activeStudents++;
                }
            }

            if (isCandidate) {
                supporterCandidates++;
            }

            StudentAnalyticsDTO studentDto = StudentAnalyticsDTO.builder()
                    .studentId(studentId)
                    .studentName(member.getUser().getFullName())
                    .username(member.getUser().getUsername())
                    .progressPercentage(progressPercentage)
                    .completedNodes(completedNodes)
                    .totalNodes(totalNodes)
                    .quizAvg(quizAvg)
                    .lastActive(lastActiveStr)
                    .supporterCandidate(isCandidate)
                    .build();

            studentAnalyticsList.add(studentDto);
        }

        int averageProgress = totalStudents > 0 ? sumProgress / totalStudents : 0;

        return SpaceAnalyticsDTO.builder()
                .totalStudents(totalStudents)
                .averageProgress(averageProgress)
                .activeStudents(activeStudents)
                .supporterCandidates(supporterCandidates)
                .students(studentAnalyticsList)
                .build();
    }

    @Override
    public List<CompletedNodeResponse> getCompletedNodesDetail(Long spaceId, Long studentId) {
        java.util.List<NodeProgress> progresses = nodeProgressRepository.findCompletedNodesByStudentAndSpace(studentId, spaceId);
        return progresses.stream().map(p -> CompletedNodeResponse.builder()
                .nodeId(p.getLearningNode().getId())
                .nodeName(p.getLearningNode().getTitle())
                .completedAt(p.getCompletedAt() != null ? p.getCompletedAt() : p.getUpdatedAt())
                .build()).collect(java.util.stream.Collectors.toList());
    }
}
