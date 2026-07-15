package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.SubmitCodeRequest;
import com.example.flippedclass.dto.request.TestCaseDTO;
import com.example.flippedclass.dto.response.LearningNodeResponse;
import com.example.flippedclass.dto.response.TestResultResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface GlobalLearningNodeService {
    
    List<Map<String, Object>> getAllNodesForDropdown();
    
    LearningNodeResponse getNodeDetail(Long nodeId, Authentication authentication);
    
    List<TestCaseDTO> getTestCases(Long nodeId);
    
    void saveTestCases(Long nodeId, List<TestCaseDTO> dtos);
    
    TestResultResponse submitCode(Long nodeId, SubmitCodeRequest request, Authentication authentication);
}
