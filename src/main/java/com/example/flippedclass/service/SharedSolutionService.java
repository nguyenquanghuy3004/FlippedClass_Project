package com.example.flippedclass.service;

import com.example.flippedclass.dto.SharedSolutionDto;
import com.example.flippedclass.entity.SharedSolution;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SharedSolutionService {
    boolean checkSolutionStatus(Long nodeId, String username);
    List<SharedSolutionDto> getSolutionByNode(Long nodeId);
    Map<String, Object> createSolution(Long nodeId, Map<String, String> payload, String username);
    Map<String, Object> upvoteSolution(Long solutionId);
}
