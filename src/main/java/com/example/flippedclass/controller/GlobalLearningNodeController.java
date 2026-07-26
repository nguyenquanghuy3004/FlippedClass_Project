package com.example.flippedclass.controller;

import com.example.flippedclass.annotation.LogUserActivity;
import com.example.flippedclass.dto.request.SubmitCodeRequest;
import com.example.flippedclass.dto.request.TestCaseDTO;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.dto.response.TestResultResponse;
import com.example.flippedclass.service.GlobalLearningNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-nodes")
@RequiredArgsConstructor
public class GlobalLearningNodeController {

    private final GlobalLearningNodeService globalLearningNodeService;

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNodesForDropdown() {
        return ResponseEntity.ok(globalLearningNodeService.getAllNodesForDropdown());
    }

    @LogUserActivity(actionType = "VIEW_NODE", description = "'Học bài học: ' + #result.body.title")
    @GetMapping("/{nodeId}")
    public ResponseEntity<LearningNodeResponse> getNodeDetail(@PathVariable Long nodeId, Authentication authentication) {
        return ResponseEntity.ok(globalLearningNodeService.getNodeDetail(nodeId, authentication));
    }

    @GetMapping("/{nodeId}/test-cases")
    public ResponseEntity<List<TestCaseDTO>> getTestCases(@PathVariable Long nodeId) {
        return ResponseEntity.ok(globalLearningNodeService.getTestCases(nodeId));
    }

    @PutMapping("/{nodeId}/test-cases")
    public ResponseEntity<?> saveTestCases(@PathVariable Long nodeId, @RequestBody List<TestCaseDTO> dtos) {
        globalLearningNodeService.saveTestCases(nodeId, dtos);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{nodeId}/submit-code")
    public ResponseEntity<?> submitCode(@PathVariable Long nodeId, @RequestBody SubmitCodeRequest request, Authentication authentication) {
        try {
            TestResultResponse response = globalLearningNodeService.submitCode(nodeId, request, authentication);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode().isSameCodeAs(org.springframework.http.HttpStatus.BAD_REQUEST)) {
                return ResponseEntity.badRequest().body(Map.of("message", e.getReason()));
            }
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
