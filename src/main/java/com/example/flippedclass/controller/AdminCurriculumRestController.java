package com.example.flippedclass.controller;

import com.example.flippedclass.dto.AdminLearningNodeDto;
import com.example.flippedclass.dto.AdminLearningPathDto;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.service.AdminCurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/curriculum")
@RequiredArgsConstructor
public class AdminCurriculumRestController {

    private final AdminCurriculumService adminCurriculumService;

    @GetMapping("/paths")
    public ResponseEntity<Page<AdminLearningPathDto>> getAllPaths(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LearningPathStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminCurriculumService.getAllPaths(keyword, status, pageable));
    }

    @PutMapping("/paths/{id}/status")
    public ResponseEntity<MessageResponse> updatePathStatus(@PathVariable Long id, @RequestParam LearningPathStatus status) {
        adminCurriculumService.updatePathStatus(id, status);
        return ResponseEntity.ok(new MessageResponse("Path status updated"));
    }

    @GetMapping("/nodes")
    public ResponseEntity<Page<AdminLearningNodeDto>> getAllNodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminCurriculumService.getAllNodes(keyword, status, pageable));
    }

    @PutMapping("/nodes/{id}/status")
    public ResponseEntity<MessageResponse> updateNodeStatus(@PathVariable Long id, @RequestParam String status) {
        adminCurriculumService.updateNodeStatus(id, status);
        return ResponseEntity.ok(new MessageResponse("Node status updated"));
    }
}
