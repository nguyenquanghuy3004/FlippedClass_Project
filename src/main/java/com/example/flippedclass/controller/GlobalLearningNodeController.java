package com.example.flippedclass.controller;

import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.repository.LearningNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-nodes")
public class GlobalLearningNodeController {

    @Autowired
    private LearningNodeRepository learningNodeRepository;

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNodesForDropdown() {
        List<LearningNode> nodes = learningNodeRepository.findAll();
        List<Map<String, Object>> response = nodes.stream().map(node -> Map.<String, Object>of(
                "id", node.getId(),
                "title", node.getTitle(),
                "name", node.getTitle()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
