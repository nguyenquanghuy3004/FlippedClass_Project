package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.CreateLearningNodeItemRequest;
import com.example.flippedclass.dto.response.LearningNodeItemResponse;
import com.example.flippedclass.service.FileStorageService;
import com.example.flippedclass.service.LearningNodeItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-nodes/{nodeId}/items")
public class LearningNodeController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LearningNodeItemService learningNodeItemService;

    @PostMapping
    public ResponseEntity<LearningNodeItemResponse> create(
            @PathVariable Long nodeId,
            @Valid @RequestBody CreateLearningNodeItemRequest request) {
        return ResponseEntity.ok(learningNodeItemService.createItem(nodeId, request));
    }

    @PostMapping("/upload-video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeFile(file);
        return ResponseEntity.ok(Map.of(
                "url", url,
                "fullUrl", fileStorageService.toFullUrl(url)
        ));
    }

    @GetMapping
    public ResponseEntity<?> getItems(@PathVariable Long nodeId) {
        return ResponseEntity.ok(learningNodeItemService.getItemByNodeId(nodeId));
    }
}
