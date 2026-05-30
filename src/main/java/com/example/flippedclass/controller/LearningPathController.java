package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.LearningPathRequest;
import com.example.flippedclass.dto.response.LearningPathResponse;
import com.example.flippedclass.service.LearningPathService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Quản lý các API liên quan đến Learning Path
@RestController
@RequestMapping("/api/learning-spaces/{spaceId}/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    // Lấy danh sách Learning Path theo Learning Space
    @GetMapping
    public List<LearningPathResponse> findByLearningSpace(@PathVariable Long spaceId) {
        return learningPathService.findByLearningSpace(spaceId);
    }

    // Tạo mới Learning Path trong Learning Space
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LearningPathResponse create(
            @PathVariable Long spaceId,
            @Valid @RequestBody LearningPathRequest request
    ) {
        return learningPathService.create(spaceId, request);
    }

    // Lấy chi tiết một Learning Path
    @GetMapping("/{pathId}")
    public LearningPathResponse findById(@PathVariable Long spaceId, @PathVariable Long pathId) {
        return learningPathService.findById(spaceId, pathId);
    }

    // Cập nhật thông tin Learning Path
    @PutMapping("/{pathId}")
    public LearningPathResponse update(
            @PathVariable Long spaceId,
            @PathVariable Long pathId,
            @Valid @RequestBody LearningPathRequest request
    ) {
        return learningPathService.update(spaceId, pathId, request);
    }

    // Xóa Learning Path theo id
    @DeleteMapping("/{pathId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long spaceId, @PathVariable Long pathId) {
        learningPathService.delete(spaceId, pathId);
    }
}
