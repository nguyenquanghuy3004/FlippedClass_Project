package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateLearningPathRequest;
import com.example.flippedclass.dto.request.ReorderLearningPathRequest;
import com.example.flippedclass.dto.request.UpdateLearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.service.LearningPathService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.flippedclass.service.LearningNodeService;
import com.example.flippedclass.dto.request.CreateLearningNodeRequest;
import com.example.flippedclass.dto.response.LearningNodeResponse;

import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-spaces/{spaceId}/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;
    private final LearningNodeService learningNodeService;

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PostMapping
    public ResponseEntity<LearningPathResponse> create(@PathVariable Long spaceId,@Valid @RequestBody CreateLearningPathRequest request) {
        return ResponseEntity.ok(learningPathService.createLearningPath(spaceId, request));
    }

    @com.example.flippedclass.annotation.LogUserActivity(actionType = "CREATE_NODE", description = "'Tạo mới bài học: ' + #request.title")
    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PostMapping("/{pathId}/learning-nodes")
    public ResponseEntity<LearningNodeResponse> createLearningNode( @PathVariable Long spaceId, @PathVariable Long pathId,
              @Valid @RequestBody CreateLearningNodeRequest request) {
        return ResponseEntity.ok(learningNodeService.createLearningNode(pathId, request));
    }

    @PreAuthorize("@spaceSecurity.isMemberInSpace(#spaceId)")
    @GetMapping
    public ResponseEntity<List<LearningPathResponse>> getLearningPath(@PathVariable Long spaceId, Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            userId = userDetails.getId();
        }
        return ResponseEntity
                .ok(learningPathService.getLearningPath(spaceId, userId));
    }

    @PreAuthorize("@spaceSecurity.isMemberInSpace(#spaceId)")
    @GetMapping("/{pathId}")
    public ResponseEntity<LearningPathResponse> getDetail(@PathVariable Long spaceId, @PathVariable Long pathId, Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            userId = userDetails.getId();
        }
        return ResponseEntity
                .ok(learningPathService.getLearningPathDetail(spaceId, pathId, userId));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PutMapping("/{pathId}")
    public ResponseEntity<LearningPathResponse> update(@PathVariable Long spaceId, @PathVariable Long pathId,@Valid @RequestBody UpdateLearningPathRequest request) {
        return ResponseEntity
                .ok(learningPathService.updateLearningPath(spaceId, pathId, request));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PutMapping("/{pathId}/archive")
    public ResponseEntity<?> archive(@PathVariable Long spaceId, @PathVariable Long pathId) {
        learningPathService.archiveLearningPath(spaceId, pathId);
        return ResponseEntity
                .ok(new MessageResponse("Lưu trữ module thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PutMapping("/{pathId}/restore")
    public ResponseEntity<?> restore(@PathVariable Long spaceId, @PathVariable Long pathId) {
        learningPathService.restoreLearningPath(spaceId, pathId);
        return ResponseEntity.ok(new MessageResponse("Khôi phục module thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @DeleteMapping("/{pathId}")
    public ResponseEntity<?> delete(@PathVariable Long spaceId, @PathVariable Long pathId) {
        learningPathService.deleteLearningPathModul(spaceId, pathId);
        return ResponseEntity.ok(new MessageResponse("Xóa module thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @DeleteMapping
    public ResponseEntity<?> deleteAll(@PathVariable Long spaceId) {
        learningPathService.deleteAllLearningPaths(spaceId);
        return ResponseEntity.ok(new MessageResponse("Xóa toàn bộ roadmap thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PutMapping("/reorder")
    public ResponseEntity<?> reorder(@PathVariable Long spaceId, @Valid @RequestBody ReorderLearningPathRequest request) {
        learningPathService.reorderLearningPaths(spaceId, request);
        return ResponseEntity.ok(new MessageResponse("Cập nhật thứ tự thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @DeleteMapping("/{pathId}/learning-nodes/{nodeId}")
    public ResponseEntity<?> deleteNode( @PathVariable Long spaceId,@PathVariable Long pathId,
            @PathVariable Long nodeId) {
        learningNodeService.deleteNode(nodeId);
        return ResponseEntity.ok(new MessageResponse("Xóa bài học thành công"));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @PutMapping("/{pathId}/learning-nodes/{nodeId}")
    public ResponseEntity<LearningNodeResponse> updateNode(@PathVariable Long spaceId, @PathVariable Long pathId,
            @PathVariable Long nodeId,
                @Valid @RequestBody CreateLearningNodeRequest request) {
        return ResponseEntity.ok(learningNodeService.updateLearningNode(nodeId, request));
    }

    @PreAuthorize("@spaceSecurity.hasRoleInSpace(#spaceId, 'OWNER', 'SUPPORTER')")
    @GetMapping("/deleted")
    public ResponseEntity<List<LearningPathResponse>> getDeletedLearningPaths(@PathVariable Long spaceId) {
        return ResponseEntity.ok(learningPathService.getDeletedLearningPaths(spaceId));
    }
}
