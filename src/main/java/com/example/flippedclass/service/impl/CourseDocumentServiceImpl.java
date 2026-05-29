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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseDocumentServiceImpl implements CourseDocumentService {

    private final CourseDocumentRepository documentRepository;
    private final LearningPathService learningPathService;

    public CourseDocumentServiceImpl(CourseDocumentRepository documentRepository, LearningPathService learningPathService) {
        this.documentRepository = documentRepository;
        this.learningPathService = learningPathService;
    }

    @Override
    public List<CourseDocumentResponse> findByLearningPath(Long learningPathId) {
        learningPathService.getLearningPath(learningPathId);
        return documentRepository.findByLearningPathId(learningPathId).stream()
                .map(CourseDocumentResponse::from)
                .toList();
    }

    @Override
    public CourseDocumentResponse create(Long learningPathId, CourseDocumentRequest request) {
        LearningPath learningPath = learningPathService.getLearningPath(learningPathId);
        CourseDocument document = new CourseDocument();
        document.setLearningPath(learningPath);
        applyRequest(document, request);
        return CourseDocumentResponse.from(documentRepository.save(document));
    }

    @Override
    public CourseDocumentResponse update(Long documentId, CourseDocumentRequest request) {
        CourseDocument document = getDocument(documentId);
        applyRequest(document, request);
        return CourseDocumentResponse.from(documentRepository.save(document));
    }

    @Override
    public void delete(Long documentId) {
        documentRepository.delete(getDocument(documentId));
    }

    private CourseDocument getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course document not found"));
    }

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
