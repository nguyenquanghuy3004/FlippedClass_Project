package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import com.example.flippedclass.service.CourseDocumentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Quản lý các API tài liệu trong Learning Path
@RestController
@RequestMapping("/api/learning-paths/{learningPathId}/documents")
@RequiredArgsConstructor
public class CourseDocumentController {

    private final CourseDocumentService documentService;

    // Lấy danh sách tài liệu theo Learning Path
    @GetMapping
    public List<CourseDocumentResponse> findByLearningPath(@PathVariable Long learningPathId) {
        return documentService.findByLearningPath(learningPathId);
    }

    // Tạo mới tài liệu cho Learning Path
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDocumentResponse create(
            @PathVariable Long learningPathId,
            @Valid @RequestBody CourseDocumentRequest request
    ) {
        return documentService.create(learningPathId, request);
    }

    // Cập nhật thông tin tài liệu học tập
    @PutMapping("/{documentId}")
    public CourseDocumentResponse update(
            @PathVariable Long learningPathId,
            @PathVariable Long documentId,
            @Valid @RequestBody CourseDocumentRequest request
    ) {
        return documentService.update(learningPathId, documentId, request);
    }

    // Xóa tài liệu khỏi Learning Path
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long learningPathId, @PathVariable Long documentId) {
        documentService.delete(learningPathId, documentId);
    }
}
