package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.entity.*;
import com.example.flippedclass.enums.MemberRole;
import com.example.flippedclass.enums.PeerPairingStatus;
import com.example.flippedclass.repository.*;
import com.example.flippedclass.service.PeerMentoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeerMentoringServiceImpl implements PeerMentoringService {

    private final LearningSpaceMemberRepository memberRepository;
    private final GradeEntryRepository gradeEntryRepository;
    private final PeerPairingRepository peerPairingRepository;
    private final LearningSpaceRepository learningSpaceRepository;

    @Override
    public double calculateGpaForStudent(Long spaceId, Long studentId) {
        List<GradeEntry> grades = gradeEntryRepository.findBySession_LearningPath_LearningSpace_IdAndStudentId(spaceId, studentId);
        if (grades.isEmpty()) return 0.0;
        
        double totalScore = 0;
        double totalMaxScore = 0;

        for (GradeEntry entry : grades) {
            double score = entry.getScore() != null ? entry.getScore().doubleValue() : 0.0;
            double maxScore = entry.getCriterion().getMaxScore() != null ? entry.getCriterion().getMaxScore().doubleValue() : 0.0;
            if (maxScore > 0) {
                totalScore += score;
                totalMaxScore += maxScore;
            }
        }
        
        if (totalMaxScore == 0) return 0.0;
        return (totalScore / totalMaxScore) * 10.0; // GPA on a 10.0 scale
    }

    @Override
    public Map<String, List<Map<String, Object>>> classifyStudents(Long spaceId) {
        List<LearningSpaceMember> members = memberRepository.findByLearningSpaceId(spaceId).stream()
                .filter(m -> m.getRole() == MemberRole.MEMBER || m.getRole() == MemberRole.SUPPORTER)
                .toList();

        List<StudentGpaInfo> studentGpas = members.stream()
                .map(m -> new StudentGpaInfo(m.getUser(), calculateGpaForStudent(spaceId, m.getUser().getId()), m.getRole()))
                .sorted(Comparator.comparingDouble(StudentGpaInfo::getGpa).reversed())
                .toList();

        int total = studentGpas.size();
        int top25Count = (int) Math.ceil(total * 0.25);
        int bottom25Count = (int) Math.ceil(total * 0.25);

        List<Map<String, Object>> strong = new ArrayList<>();
        List<Map<String, Object>> average = new ArrayList<>();
        List<Map<String, Object>> weak = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            StudentGpaInfo info = studentGpas.get(i);
            UserResponse response = UserServiceImpl.toResponse(info.getUser());
            boolean isSupporter = info.getRole() == MemberRole.SUPPORTER;
            List<UserResponse> mentees = new ArrayList<>();
            
            if (isSupporter) {
                mentees = getMentees(spaceId, info.getUser().getId());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("user", response);
            map.put("isSupporter", isSupporter);
            map.put("mentees", mentees);

            if (i < top25Count) {
                strong.add(map);
            } else if (i >= total - bottom25Count) {
                weak.add(map);
            } else {
                average.add(map);
            }
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("STRONG", strong);
        result.put("AVERAGE", average);
        result.put("WEAK", weak);
        return result;
    }

    @Override
    @Transactional
    public void autoMatchPairs(Long spaceId) {
        LearningSpace space = learningSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        Map<String, List<Map<String, Object>>> classified = classifyStudents(spaceId);
        List<UserResponse> strongStudents = classified.get("STRONG").stream().map(m -> (UserResponse) m.get("user")).collect(Collectors.toList());
        List<UserResponse> weakStudents = classified.get("WEAK").stream().map(m -> (UserResponse) m.get("user")).collect(Collectors.toList());

        // Existing active pairs
        List<PeerPairing> existingPairs = peerPairingRepository.findByLearningSpace_Id(spaceId).stream()
                .filter(p -> p.getStatus() == PeerPairingStatus.ACTIVE)
                .toList();

        Set<Long> alreadyPairedMentees = existingPairs.stream()
                .map(p -> p.getMentee().getId())
                .collect(Collectors.toSet());

        // We only map members whose role is SUPPORTER as mentors.
        // Or if auto-matching should only consider SUPPORTERs?
        // Prompt: "Mỗi sinh viên thuộc nhóm STRONG có thể được ghép với tối đa 5 sinh viên thuộc nhóm WEAK."
        // We will match ANY STRONG student or just SUPPORTERS? 
        // The prompt says: "Giảng viên có thể... Thay đổi role của sinh viên từ Student sang Supporter... Chỉ định sinh viên mạnh làm Supporter để hỗ trợ các sinh viên yếu"
        // Let's assume ONLY students who are STRONG *AND* have been promoted to SUPPORTER can be matched.
        // Wait, the prompt says "Hệ thống hoặc giảng viên thực hiện ghép Supporter với tối đa 5 sinh viên yếu".
        // Let's filter strongStudents by those who are already SUPPORTERs.
        
        List<LearningSpaceMember> members = memberRepository.findByLearningSpaceId(spaceId);
        Set<Long> supporterIds = members.stream()
                .filter(m -> m.getRole() == MemberRole.SUPPORTER)
                .map(m -> m.getUser().getId())
                .collect(Collectors.toSet());

        List<UserResponse> availableMentors = strongStudents.stream()
                .filter(s -> supporterIds.contains(s.getId()))
                .collect(Collectors.toList());
                
        // If there are no supporters, maybe we auto-promote STRONG to SUPPORTER? 
        // "Giảng viên có thể thay đổi role ... chỉ định sinh viên mạnh làm Supporter". So we rely on Lecturer.

        List<UserResponse> unpairedWeak = weakStudents.stream()
                .filter(w -> !alreadyPairedMentees.contains(w.getId()))
                .collect(Collectors.toList());

        Collections.shuffle(unpairedWeak);
        Collections.shuffle(availableMentors);

        int weakIndex = 0;
        for (UserResponse mentorResp : availableMentors) {
            long currentMenteeCount = peerPairingRepository.countByLearningSpace_IdAndMentor_IdAndStatus(spaceId, mentorResp.getId(), PeerPairingStatus.ACTIVE);
            while (currentMenteeCount < 5 && weakIndex < unpairedWeak.size()) {
                UserResponse menteeResp = unpairedWeak.get(weakIndex);
                
                User mentor = new User(); mentor.setId(mentorResp.getId());
                User mentee = new User(); mentee.setId(menteeResp.getId());

                Optional<PeerPairing> existingPairOpt = peerPairingRepository.findByLearningSpace_IdAndMentee_IdAndMentor_Id(spaceId, menteeResp.getId(), mentorResp.getId());

                if (existingPairOpt.isPresent()) {
                    PeerPairing existingPair = existingPairOpt.get();
                    existingPair.setStatus(PeerPairingStatus.ACTIVE);
                    peerPairingRepository.save(existingPair);
                } else {
                    PeerPairing newPair = PeerPairing.builder()
                            .learningSpace(space)
                            .mentor(mentor)
                            .mentee(mentee)
                            .status(PeerPairingStatus.ACTIVE)
                            .build();

                    peerPairingRepository.save(newPair);
                }
                
                currentMenteeCount++;
                weakIndex++;
            }
            if (weakIndex >= unpairedWeak.size()) {
                break;
            }
        }
    }

    @Override
    @Transactional
    public void promoteToSupporter(Long spaceId, Long studentId) {
        LearningSpaceMember member = memberRepository.findByLearningSpaceId(spaceId).stream()
                .filter(m -> m.getUser().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not found in space"));

        // Check if student belongs to STRONG group
        Map<String, List<Map<String, Object>>> classified = classifyStudents(spaceId);
        List<Map<String, Object>> strongGroup = classified.get("STRONG");
        boolean isStrong = strongGroup.stream()
                .anyMatch(m -> {
                    UserResponse u = (UserResponse) m.get("user");
                    return u.getId().equals(studentId);
                });
                
        if (!isStrong) {
            throw new RuntimeException("Chỉ cho phép thăng cấp thành viên thuộc nhóm học tập tốt (STRONG) làm Supporter!");
        }

        if (member.getRole() == MemberRole.MEMBER) {
            member.setRole(MemberRole.SUPPORTER);
            memberRepository.save(member);
        }
    }

    @Override
    public List<UserResponse> getMentees(Long spaceId, Long mentorId) {
        return peerPairingRepository.findByLearningSpace_IdAndMentor_Id(spaceId, mentorId).stream()
                .filter(p -> p.getStatus() == PeerPairingStatus.ACTIVE)
                .map(p -> UserServiceImpl.toResponse(p.getMentee()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeAllPairingsForMentor(Long spaceId, Long mentorId) {
        List<PeerPairing> pairings = peerPairingRepository.findByLearningSpace_IdAndMentor_Id(spaceId, mentorId);
        for (PeerPairing pairing : pairings) {
            if (pairing.getStatus() == PeerPairingStatus.ACTIVE) {
                pairing.setStatus(PeerPairingStatus.INACTIVE);
                peerPairingRepository.save(pairing);
            }
        }
    }

    @Override
    @Transactional
    public void removeAllPairingsForMember(Long spaceId, Long memberId) {
        removeAllPairingsForMentor(spaceId, memberId);
        List<PeerPairing> menteePairings = peerPairingRepository.findByLearningSpace_IdAndMentee_Id(spaceId, memberId);
        for (PeerPairing pairing : menteePairings) {
            if (pairing.getStatus() == PeerPairingStatus.ACTIVE) {
                pairing.setStatus(PeerPairingStatus.INACTIVE);
                peerPairingRepository.save(pairing);
            }
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class StudentGpaInfo {
        private User user;
        private double gpa;
        private MemberRole role;
    }
}
