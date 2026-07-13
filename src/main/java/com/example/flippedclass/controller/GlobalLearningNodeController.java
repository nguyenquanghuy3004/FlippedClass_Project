package com.example.flippedclass.controller;

import com.example.flippedclass.annotation.LogUserActivity;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.flippedclass.service.LocalCompilerService;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.flippedclass.dto.request.SubmitCodeRequest;
import com.example.flippedclass.dto.request.TestCaseDTO;
import com.example.flippedclass.dto.response.TestResultResponse;
import com.example.flippedclass.entity.TestCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-nodes")
@RequiredArgsConstructor
public class GlobalLearningNodeController {

    private final LearningNodeRepository learningNodeRepository;
    private final NodeConnectionRepository nodeConnectionRepository;
    private final UserRepository userRepository;
    private final NodeProgressRepository nodeProgressRepository;
    private final TestCaseRepository testCaseRepository;
    private final LocalCompilerService localCompilerService;

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNodesForDropdown() {
        List<LearningNode> nodes = learningNodeRepository.findAll();
        List<Map<String, Object>> response = nodes.stream().map(node -> {
            String spaceName = "Uncategorized";
            if (node.getLearningPath() != null && node.getLearningPath().getLearningSpace() != null) {
                spaceName = node.getLearningPath().getLearningSpace().getName();
            }
            return Map.<String, Object>of(
                    "id", node.getId(),
                    "title", node.getTitle(),
                    "name", node.getTitle(),
                    "spaceName", spaceName,
                    "nodeType", node.getNodeType() != null ? node.getNodeType() : ""
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @LogUserActivity(actionType = "VIEW_NODE", description = "'Học bài học: ' + #result.body.title")
    @GetMapping("/{nodeId}")
    @Transactional(readOnly = true)
    public ResponseEntity<LearningNodeResponse> getNodeDetail(@PathVariable Long nodeId, Authentication authentication) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        Long prereqId = null;
        var connections = nodeConnectionRepository.findByTargetNodeId(nodeId);
        if (!connections.isEmpty()) {
            prereqId = connections.get(0).getSourceNode().getId();
        }

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            boolean isStudent = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("STUDENT"));

            if (isStudent && prereqId != null && !"DOCUMENT".equals(node.getNodeType())) {
                var progress = nodeProgressRepository.findByStudentIdAndLearningNodeId(userDetails.getId(), prereqId).orElse(null);
                if (progress == null || !progress.getStatus().name().equals("COMPLETED")) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chưa hoàn thành bài học trước đó!");
                }
            }
        }

        return ResponseEntity.ok(LearningNodeResponse.builder()
                .id(node.getId())
                .title(node.getTitle())
                .learningPathId(node.getLearningPath() != null ? node.getLearningPath().getId() : null)
                .learningSpaceId((node.getLearningPath() != null && node.getLearningPath().getLearningSpace() != null) ? node.getLearningPath().getLearningSpace().getId() : null)
                .status(node.getStatus())
                .nodeType(node.getNodeType())
                .content(node.getContent() != null ? node.getContent() : node.getDescription())
                .starterCode(node.getStarterCode())
                .solutionCode(node.getSolutionCode())
                .prerequisiteNodeId(prereqId)
                .quizzes(node.getQuizzes() != null ? node.getQuizzes().stream().map(q -> Map.<String, Object>of("id", q.getId(), "title", q.getTitle())).collect(Collectors.toList()) : List.of())
                .createdAt(node.getCreatedAt())
                .updatedAt(node.getUpdatedAt())
                .build());
    }

    @GetMapping("/{nodeId}/test-cases")
    public ResponseEntity<List<TestCaseDTO>> getTestCases(@PathVariable Long nodeId) {
        List<TestCase> testCases = testCaseRepository.findByLearningNodeIdOrderByCreatedAtAsc(nodeId);
        List<TestCaseDTO> dtos = testCases.stream().map(tc -> TestCaseDTO.builder()
                .id(tc.getId())
                .inputData(tc.getInputData())
                .expectedOutput(tc.getExpectedOutput())
                .isHidden(tc.getIsHidden())
                .points(tc.getPoints())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{nodeId}/test-cases")
    @Transactional
    public ResponseEntity<?> saveTestCases(@PathVariable Long nodeId, @RequestBody List<TestCaseDTO> dtos) {
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        testCaseRepository.deleteByLearningNodeId(nodeId);

        List<TestCase> testCases = dtos.stream().map(dto -> TestCase.builder()
                .learningNode(node)
                .inputData(dto.getInputData())
                .expectedOutput(dto.getExpectedOutput())
                .isHidden(dto.getIsHidden() != null ? dto.getIsHidden() : false)
                .points(dto.getPoints() != null ? dto.getPoints() : 10)
                .build()).collect(Collectors.toList());

        testCaseRepository.saveAll(testCases);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{nodeId}/submit-code")
    public ResponseEntity<?> submitCode(@PathVariable Long nodeId, @RequestBody SubmitCodeRequest request, Authentication authentication) {
        LearningNode node = learningNodeRepository.findById(nodeId).orElse(null);
        if (node == null) {
            return ResponseEntity.notFound().build();
        }

        List<TestCase> testCases = testCaseRepository.findByLearningNodeIdOrderByCreatedAtAsc(nodeId);
        if (testCases.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No Test Cases have been configured for this node."));
        }

        String studentCode = request.getCode();
        List<TestResultResponse.TestCaseResult> results = new ArrayList<>();
        int totalScore = 0;
        int maxScore = 0;
        int passedCount = 0;

        for (TestCase tc : testCases) {
            int points = tc.getPoints() != null ? tc.getPoints() : 10;
            maxScore += points;

            try {
                // Chạy qua Local Engine
                String rawOutput = localCompilerService.executeJavaCode(studentCode, tc.getInputData());
                String actualOutput = rawOutput != null ? rawOutput : "";
                String expectedOut = tc.getExpectedOutput() != null ? tc.getExpectedOutput().trim() : "";

                // Chuẩn hóa \r\n thành \n
                actualOutput = actualOutput.trim().replace("\r\n", "\n");
                expectedOut = expectedOut.replace("\r\n", "\n");

                boolean passed = actualOutput.equals(expectedOut);

                if (passed) {
                    totalScore += points;
                    passedCount++;
                }

                TestResultResponse.TestCaseResult result = TestResultResponse.TestCaseResult.builder()
                        .testCaseId(tc.getId())
                        .passed(passed)
                        .hidden(tc.getIsHidden() != null ? tc.getIsHidden() : false)
                        .points(points)
                        .build();

                if (tc.getIsHidden() == null || !tc.getIsHidden()) {
                    result.setInputData(tc.getInputData());
                    result.setExpectedOutput(tc.getExpectedOutput());
                    result.setActualOutput(actualOutput);
                }

                results.add(result);

            } catch (Exception e) {
                TestResultResponse.TestCaseResult result = TestResultResponse.TestCaseResult.builder()
                        .testCaseId(tc.getId())
                        .passed(false)
                        .hidden(tc.getIsHidden())
                        .points(points)
                        .actualOutput("Server Error: " + e.getMessage())
                        .build();
                results.add(result);
            }
        }

        boolean allPassed = (passedCount == testCases.size());

        if (allPassed && authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            com.example.flippedclass.entity.User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                com.example.flippedclass.entity.NodeProgress progress = nodeProgressRepository
                        .findByStudentIdAndLearningNodeId(user.getId(), nodeId)
                        .orElseGet(() -> com.example.flippedclass.entity.NodeProgress.builder()
                                .student(user)
                                .learningNode(node)
                                .build());

                progress.setStatus(com.example.flippedclass.enums.ProgressStatus.COMPLETED);
                progress.setCompletedAt(java.time.LocalDateTime.now());
                nodeProgressRepository.save(progress);
            }
        }

        TestResultResponse finalResponse = TestResultResponse.builder()
                .allPassed(allPassed)
                .totalScore(totalScore)
                .maxScore(maxScore)
                .passedCount(passedCount)
                .totalCases(testCases.size())
                .results(results)
                .build();

        return ResponseEntity.ok(finalResponse);
    }
}
