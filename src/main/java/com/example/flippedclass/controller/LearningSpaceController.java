package com.example.flippedclass.controller;

import com.example.flippedclass.annotation.LogUserActivity;
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

    @LogUserActivity(actionType = "CREATE_SPACE", description = "User created a new learning space")
    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @PostMapping
    @Transactional
    public ResponseEntity<LearningSpaceResponse> createLearningSpace(@Valid @RequestBody CreateLearningSpaceRequest request) {
        LearningSpaceResponse response = learningSpaceService.createLearningSpace(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('STUDENT')")
    @GetMapping("/my-spaces")
    public ResponseEntity<List<LearningSpaceResponse>> getMySpaces() {
        return ResponseEntity.ok(learningSpaceService.getMySpaces());
    }


    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN') or hasAuthority('STUDENT')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteLearningSpace (@PathVariable Long id){
        learningSpaceService.deleteLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Successfully deleted"));
    }


    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreLearningSpace(@PathVariable Long id){
        learningSpaceService.restoreLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Successfully restored"));
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveLearningSpace(@PathVariable Long id){
        learningSpaceService.archiveLearningSpace(id);
        return ResponseEntity.ok(new MessageResponse("Successfully archived"));
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<LearningSpaceResponse> cloneLearningSpace(@PathVariable Long id, @RequestParam String newName) {
        LearningSpaceResponse response = learningSpaceService.cloneSpace(id, newName);
        return ResponseEntity.ok(response);
    }

    @LogUserActivity(actionType = "JOIN_SPACE", description = "User joined a learning space")
    @PostMapping("/join")
    public ResponseEntity<JoinLearningSpaceResponse>joinLearningSpace(@RequestBody JoinLearningSpaceRequest request){
        return ResponseEntity.ok(learningSpaceService.joinLearningSpace(request));
    }

    @PreAuthorize("hasAuthority('MENTOR') or hasAuthority('ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateLearingSpace(@PathVariable Long id, @RequestBody LearningSpace spaceDetail){
         LearningSpaceResponse update =  learningSpaceService.updateLearningSpace(id, spaceDetail);
         return ResponseEntity.ok(update);
    }

    @GetMapping("/public")
    public ResponseEntity<List<LearningSpaceResponse>> getPublicSpaces() {
        return ResponseEntity.ok(learningSpaceService.getPublicSpaces());
    }

    @GetMapping("/invite-code/{inviteCode}")
    public ResponseEntity<LearningSpaceResponse> getSpaceByInviteCode(@PathVariable String inviteCode) {
        return ResponseEntity.ok(learningSpaceService.getSpaceByInviteCode(inviteCode));
    }
}
