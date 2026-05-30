package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import com.example.flippedclass.entity.CourseDocument;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.repository.CourseDocumentRepository;
import com.example.flippedclass.service.CourseDocumentService;
import com.example.flippedclass.service.LearningPathService;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Xử lý nghiệp vụ lưu và kiểm tra tài liệu học tập
@Service
@RequiredArgsConstructor
public class CourseDocumentServiceImpl implements CourseDocumentService {

    private final CourseDocumentRepository documentRepository;
    private final LearningPathService learningPathService;

    // Lấy tài liệu sau khi kiểm tra Learning Path có tồn tại
    @Override
    public List<CourseDocumentResponse> findByLearningPath(Long learningPathId) {
        learningPathService.getLearningPath(learningPathId);
        return documentRepository.findByLearningPathId(learningPathId).stream()
                .map(CourseDocumentResponse::from)
                .toList();
    }

    // Tạo tài liệu mới cho Learning Path
    @Override
    public CourseDocumentResponse create(Long learningPathId, CourseDocumentRequest request) {
        LearningPath learningPath = learningPathService.getLearningPath(learningPathId);
        CourseDocument document = new CourseDocument();
        document.setLearningPath(learningPath);
        applyRequest(document, request);
        return CourseDocumentResponse.from(documentRepository.save(document));
    }

    // Cập nhật thông tin tài liệu học tập
    @Override
    public CourseDocumentResponse update(Long learningPathId, Long documentId, CourseDocumentRequest request) {
        CourseDocument document = getDocumentInLearningPath(learningPathId, documentId);
        applyRequest(document, request);
        return CourseDocumentResponse.from(documentRepository.save(document));
    }

    // Xóa tài liệu học tập theo id
    @Override
    public void delete(Long learningPathId, Long documentId) {
        documentRepository.delete(getDocumentInLearningPath(learningPathId, documentId));
    }

    // Kiểm tra và lấy tài liệu trước khi xử lý
    private CourseDocument getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course document not found"));
    }

    private CourseDocument getDocumentInLearningPath(Long learningPathId, Long documentId) {
        learningPathService.getLearningPath(learningPathId);
        CourseDocument document = getDocument(documentId);
        if (!Objects.equals(document.getLearningPath().getId(), learningPathId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course document does not belong to this learning path");
        }
        return document;
    }

    // Kiểm tra dữ liệu tài liệu trước khi lưu
    private void applyRequest(CourseDocument document, CourseDocumentRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document title is required");
        }
        if (request.getDocumentType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document type is required");
        }
        validateUrl(request.getUrl());

        document.setTitle(request.getTitle().trim());
        document.setDocumentType(request.getDocumentType());
        document.setUrl(request.getUrl().trim());
        document.setDescription(request.getDescription());
    }

    // Kiểm tra đường dẫn tài liệu có đúng dạng URL
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document URL is required");
        }
        try {
            URI uri = URI.create(url.trim());
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("URL must include scheme and host");
            }
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document URL is invalid");
        }
    }
}
