package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateLearningNodeItemRequest;
import com.example.flippedclass.dto.response.LearningNodeItemResponse;
import com.example.flippedclass.service.FileStorageService;
import com.example.flippedclass.service.LearningNodeItemService;
import com.example.flippedclass.service.LearningNodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/learning-nodes/{nodeId}/items")
@RequiredArgsConstructor
public class LearningNodeController {
    @Autowired
    private LearningNodeService learningNodeService;

    private final FileStorageService fileStorageService;
    private final LearningNodeItemService learningNodeItemService;

    @PostMapping
    public ResponseEntity<LearningNodeItemResponse> create(@PathVariable Long nodeId,
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


    @PostMapping("/upload-document")
    public ResponseEntity<Map<String,String>> uploadDocument(@RequestParam("file") MultipartFile file){
        String url = fileStorageService.storePdf(file);

        return ResponseEntity.ok(Map.of(
                "url", url,
                "fullUrl", fileStorageService.toFullUrl(url)
        ));
    }

    @GetMapping
    public ResponseEntity<?> getItems(@PathVariable Long nodeId) {
        return ResponseEntity.ok(learningNodeItemService.getItemByNodeId(nodeId));
    }

        @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String,String>> deleteItem(@PathVariable Long itemId){
        learningNodeItemService.delete(itemId);
        return  ResponseEntity.ok(Map.of("message", "gỡ tài liệu thành công"));
        }

}
