package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateLearningSpaceRequest;
import com.example.flippedclass.dto.request.JoinLearningSpaceRequest;
import com.example.flippedclass.dto.response.JoinLearningSpaceResponse;
import com.example.flippedclass.dto.response.LearningSpaceResponse;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.service.LearningSpaceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-spaces")
public class LearningSpaceController {

    @Autowired
    private LearningSpaceService learningSpaceService;

    @PreAuthorize("hasAuthority('MENTOR')")
    @PostMapping
    @Transactional
    public ResponseEntity<LearningSpaceResponse> createLearningSpace(@Valid @RequestBody CreateLearningSpaceRequest request) {
        LearningSpaceResponse response = learningSpaceService.createLearningSpace(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('MENTOR')")
    @GetMapping("/my-spaces")
    public ResponseEntity<List<LearningSpaceResponse>> getMySpaces() {
        return ResponseEntity.ok(learningSpaceService.getMySpaces());
    }


    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteLearningSpace (@PathVariable Long id){
        learningSpaceService.deleteLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Xóa thành công"));
    }


    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreLearningSpace(@PathVariable Long id){
        learningSpaceService.restoreLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Khôi phục thành công "));
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveLearningSpace(@PathVariable Long id){
        learningSpaceService.archiveLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Lưu trữ thành công "));
    }

    @PostMapping("/join")
    public ResponseEntity<JoinLearningSpaceResponse>joinLearningSpace(@RequestBody JoinLearningSpaceRequest request){
        return ResponseEntity.ok(learningSpaceService.joinLearningSpace(request));
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateLearingSpace(@PathVariable Long id, @RequestBody LearningSpace spaceDetail){
         LearningSpace update =  learningSpaceService.updateLearningSpace(id, spaceDetail);
            return ResponseEntity.ok(update);
    }
}
