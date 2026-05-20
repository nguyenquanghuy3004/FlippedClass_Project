package com.example.flippedclass.controller;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.res.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.res.LearningSpaceResponse;
import com.example.flippedclass.dto.res.MessageResponse;
import com.example.flippedclass.service.LearningSpaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-spaces")
public class LearningSpaceController {

    private final LearningSpaceService learningSpaceService;

    public LearningSpaceController(LearningSpaceService learningSpaceService) {
        this.learningSpaceService = learningSpaceService;
    }

    @PostMapping
    public ResponseEntity<LearningSpaceResponse> createLearningSpace(
            @Valid @RequestBody CreateLearningSpaceRequest request) {
        return ResponseEntity.ok(learningSpaceService.createLearningSpace(request));
    }

    @PostMapping("/join")
    public ResponseEntity<JoinLearningSpaceResponse> joinLearningSpace(
            @RequestBody JoinLearningSpaceRequest request) {
        return ResponseEntity.ok(learningSpaceService.joinLearningSpace(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLearningSpace(@PathVariable Long id) {
        learningSpaceService.deleteLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Xóa thành công"));
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<MessageResponse> restoreLearningSpace(@PathVariable Long id) {
        learningSpaceService.restoreLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Khôi phục thành công"));
    }
}
