package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.SharedSolutionDto;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.SharedSolution;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.NodeProgressRepository;
import com.example.flippedclass.repository.SharedSolutionRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.SharedSolutionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SharedSolutionServiceImpl implements SharedSolutionService {

    @Autowired
    private SharedSolutionRepository sharedSolutionRepository;
    @Autowired
    private LearningNodeRepository learningNodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NodeProgressRepository nodeProgressRepository;

    @Override
    public boolean checkSolutionStatus(Long nodeId, String username){
        if (username == null) return false;

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null)  return false;

        var progress = nodeProgressRepository.findByStudentIdAndLearningNodeId(user.getId(), nodeId).orElse(null);

        return progress != null && "COMPLETED".equals(progress.getStatus().name());
    }


    @Override
    public List<SharedSolutionDto> getSolutionByNode(Long nodeId){
        List<SharedSolution> solutions = sharedSolutionRepository.findByLearningNodeIdOrderByUpvotesDescCreatedAtDesc(nodeId);

        return solutions.stream().map(sol -> SharedSolutionDto.builder()
                .id(sol.getId())
                .learningNodeId(sol.getLearningNode().getId())
                .studentId(sol.getStudent().getId())
                .studentName(sol.getStudent().getFullName())
                .title(sol.getTitle())
                .codeContent(sol.getCodeContent())
                .createdAt(sol.getCreatedAt())
                .upvotes(sol.getUpvotes())
                .build()).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Map<String, Object> createSolution(Long nodeId, Map<String, String> payload, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LearningNode node = learningNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Learning node not found"));
        String title = payload.get("title");
        String codeContent = payload.get("codeContent");
        if (title == null || title.trim().isEmpty() || codeContent == null || codeContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and code content are required");
        }
        SharedSolution solution = SharedSolution.builder()
                .learningNode(node)
                .student(student)
                .title(title.trim())
                .codeContent(codeContent)
                .upvotes(0)
                .build();
        sharedSolutionRepository.save(solution);
        return Map.of("message", "Solution shared successfully", "id", solution.getId());
    }
    @Override
    @Transactional
    public Map<String, Object> upvoteSolution(Long solutionId) {
        SharedSolution solution = sharedSolutionRepository.findById(solutionId)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        solution.setUpvotes(solution.getUpvotes() + 1);
        sharedSolutionRepository.save(solution);

        return Map.of("message", "Upvoted successfully", "upvotes", solution.getUpvotes());
    }
}
