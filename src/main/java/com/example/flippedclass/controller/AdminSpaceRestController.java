package com.example.flippedclass.controller;


import com.example.flippedclass.dto.LearningSpaceDetailDto;
import com.example.flippedclass.dto.LearningSpaceDto;
import com.example.flippedclass.dto.response.MessageResponse;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.service.AdminSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/spaces")
public class AdminSpaceRestController {
    @Autowired
    private AdminSpaceService adminSpaceService;

    @GetMapping
    public ResponseEntity<Page<LearningSpaceDto>> getAllSpaces(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LearningSpaceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LearningSpaceDto> spaces = adminSpaceService.getAllPages(keyword, status, pageable);
        return ResponseEntity.ok(spaces);
    }
    @GetMapping("/{spaceId}")
    public ResponseEntity<LearningSpaceDetailDto> getSpaceDetail(@PathVariable Long spaceId) {
        return ResponseEntity.ok(adminSpaceService.getSpaceDetail(spaceId));
    }
    @PutMapping("/{spaceId}/status")
    public ResponseEntity<MessageResponse> updateStatus(@PathVariable Long spaceId, @RequestParam LearningSpaceStatus status) {
        adminSpaceService.updateSpaceStatus(spaceId, status);
        return ResponseEntity.ok(new MessageResponse("Space status updated to " + status));
    }

}
