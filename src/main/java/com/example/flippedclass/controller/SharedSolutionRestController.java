package com.example.flippedclass.controller;

import com.example.flippedclass.service.SharedSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shared-solutions")
public class SharedSolutionRestController {

    @Autowired
    private SharedSolutionService sharedSolutionService;

    @GetMapping("/node/{nodeId}/status")
    public ResponseEntity<?> getSolutionStatus(@PathVariable Long nodeId, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        boolean isPassed = sharedSolutionService.checkSolutionStatus(nodeId, email);
        return ResponseEntity.ok(Map.of("isPassed", isPassed));
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<?> getSolutionsByNode(@PathVariable Long nodeId) {
        return ResponseEntity.ok(sharedSolutionService.getSolutionByNode(nodeId));
    }

    @PostMapping("/node/{nodeId}")
    public ResponseEntity<?> createSolution(@PathVariable Long nodeId, @RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            return ResponseEntity.ok(sharedSolutionService.createSolution(nodeId, payload, authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{solutionId}/upvote")
    public ResponseEntity<?> upvoteSolution(@PathVariable Long solutionId) {
        try {
            return ResponseEntity.ok(sharedSolutionService.upvoteSolution(solutionId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
