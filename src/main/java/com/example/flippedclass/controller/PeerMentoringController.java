package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.UserResponse;
import com.example.flippedclass.service.PeerMentoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning-spaces/{spaceId}/mentoring")
@RequiredArgsConstructor
public class PeerMentoringController {

    private final PeerMentoringService peerMentoringService;

    @GetMapping("/students")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getStudentsClassification(@PathVariable Long spaceId) {
        Map<String, List<Map<String, Object>>> classifiedStudents = peerMentoringService.classifyStudents(spaceId);
        return ResponseEntity.ok(classifiedStudents);
    }

    @PostMapping("/promote/{studentId}")
    public ResponseEntity<Void> promoteToSupporter(@PathVariable Long spaceId, @PathVariable Long studentId) {
        peerMentoringService.promoteToSupporter(spaceId, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auto-match")
    public ResponseEntity<Void> autoMatchPairs(@PathVariable Long spaceId) {
        peerMentoringService.autoMatchPairs(spaceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mentees/{mentorId}")
    public ResponseEntity<List<UserResponse>> getMentees(@PathVariable Long spaceId, @PathVariable Long mentorId) {
        // Expose a method in PeerMentoringService to get mentees
        return ResponseEntity.ok(peerMentoringService.getMentees(spaceId, mentorId));
    }
}
