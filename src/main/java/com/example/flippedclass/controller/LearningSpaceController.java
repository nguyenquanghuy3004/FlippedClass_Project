package com.example.flippedclass.controller;

import com.example.flippedclass.dto.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.LearningSpaceResponse;
import com.example.flippedclass.service.LearningSpaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-spaces")
public class LearningSpaceController {

    @Autowired
    private LearningSpaceService learningSpaceService;

    @PostMapping
    public ResponseEntity<LearningSpaceResponse> createLearningSpace(@Valid @RequestBody CreateLearningSpaceRequest request) {
        LearningSpaceResponse response = learningSpaceService.createLearningSpace(request);
        return ResponseEntity.ok(response);
    }
}
