package com.example.flippedclass.controller;

import com.example.flippedclass.dto.req.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.req.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.res.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.res.LearningSpaceResponse;
import com.example.flippedclass.dto.res.MessageResponse;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLearningSpace (@PathVariable Long id){
        learningSpaceService.deleteLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Xóa thành công"));
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreLearningSpace(@PathVariable Long id){
        learningSpaceService.restoreLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Khôi phục thành công "));

    }

    @PostMapping("/join")
    public ResponseEntity<JoinLearningSpaceResponse>joinLearningSpace(@RequestBody JoinLearningSpaceRequest request){
        return ResponseEntity.ok(learningSpaceService.joinLearningSpace(request));
    }
}
