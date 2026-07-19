package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CourseDocumentRequest;
import com.example.flippedclass.dto.response.CourseDocumentResponse;
import com.example.flippedclass.entity.CourseDocument;
import com.example.flippedclass.entity.LearningPath;
import com.example.flippedclass.enums.DocumentType;
import com.example.flippedclass.repository.CourseDocumentRepository;
import com.example.flippedclass.service.LearningPathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseDocumentServiceImplTest {

    @Mock
    private CourseDocumentRepository documentRepository;

    @Mock
    private LearningPathService learningPathService;

    @InjectMocks
    private CourseDocumentServiceImpl courseDocumentService;

    private LearningPath learningPath;
    private CourseDocument document;

    @BeforeEach
    void setUp() {
        learningPath = new LearningPath();
        learningPath.setId(1L);

        document = new CourseDocument();
        document.setId(10L);
        document.setLearningPath(learningPath);
        document.setTitle("Old Title");
        document.setDocumentType(DocumentType.PDF.name());
        document.setUrl("http://old-url.com");
    }

    // --- create tests (12 cases) ---
    @Test
    void testCreate_Success() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("New Doc");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("http://example.com/doc.pdf");
        request.setDescription("desc");

        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        when(documentRepository.save(any(CourseDocument.class))).thenAnswer(i -> {
            CourseDocument d = i.getArgument(0);
            d.setId(10L);
            return d;
        });

        CourseDocumentResponse res = courseDocumentService.create(1L, request);

        assertEquals("New Doc", res.getTitle());
        assertEquals(DocumentType.PDF, res.getDocumentType());
        assertEquals("http://example.com/doc.pdf", res.getUrl());
        verify(documentRepository).save(any(CourseDocument.class));
    }

    @Test
    void testCreate_LearningPathNotFound() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        when(learningPathService.getLearningPathEntity(999L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(999L, request));
    }

    @Test
    void testCreate_TitleNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle(null);
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document title is required"));
    }

    @Test
    void testCreate_TitleEmptySpaces() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("   ");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document title is required"));
    }

    @Test
    void testCreate_DocumentTypeNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(null);
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document type is required"));
    }

    @Test
    void testCreate_UrlNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl(null);
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document URL is required"));
    }

    @Test
    void testCreate_UrlEmptySpaces() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("   ");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document URL is required"));
    }

    @Test
    void testCreate_UrlNoScheme() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("www.google.com");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document URL is invalid"));
    }

    @Test
    void testCreate_UrlNoHost() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("http://");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document URL is invalid"));
    }

    @Test
    void testCreate_UrlInvalidSyntax() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("http:// my link");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.create(1L, request));
        assertTrue(ex.getMessage().contains("Document URL is invalid"));
    }

    @Test
    void testCreate_DescriptionNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Doc");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("http://example.com");
        request.setDescription(null);
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        when(documentRepository.save(any(CourseDocument.class))).thenAnswer(i -> {
            CourseDocument d = i.getArgument(0);
            d.setId(10L);
            return d;
        });
        
        CourseDocumentResponse res = courseDocumentService.create(1L, request);
        assertNull(res.getDescription());
    }

    @Test
    void testCreate_DataIsTrimmed() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("  Doc  ");
        request.setDocumentType(DocumentType.WEBSITE);
        request.setUrl("  http://example.com  ");
        when(learningPathService.getLearningPathEntity(1L)).thenReturn(learningPath);
        when(documentRepository.save(any(CourseDocument.class))).thenAnswer(i -> {
            CourseDocument d = i.getArgument(0);
            d.setId(10L);
            return d;
        });
        
        CourseDocumentResponse res = courseDocumentService.create(1L, request);
        assertEquals("Doc", res.getTitle());
        assertEquals("http://example.com", res.getUrl());
    }

    // --- update tests (12 cases) ---
    @Test
    void testUpdate_Success() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Updated Title");
        request.setDocumentType(DocumentType.VIDEO);
        request.setUrl("https://video.com");
        
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(CourseDocument.class))).thenReturn(document);

        CourseDocumentResponse res = courseDocumentService.update(1L, 10L, request);
        assertEquals("Updated Title", res.getTitle());
        assertEquals(DocumentType.VIDEO, res.getDocumentType());
    }

    @Test
    void testUpdate_DocumentNotFound() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 99L, request));
        assertTrue(ex.getMessage().contains("Course document not found"));
    }

    @Test
    void testUpdate_DoesNotBelongToLearningPath() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document)); // document has LP=1
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(2L, 10L, request));
        assertTrue(ex.getMessage().contains("Course document does not belong to this learning path"));
    }

    @Test
    void testUpdate_TitleNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle(null);
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document title is required"));
    }

    @Test
    void testUpdate_TitleEmpty() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle(" ");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document title is required"));
    }

    @Test
    void testUpdate_DocumentTypeNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(null);
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document type is required"));
    }

    @Test
    void testUpdate_UrlNull() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl(null);
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document URL is required"));
    }

    @Test
    void testUpdate_UrlEmpty() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document URL is required"));
    }

    @Test
    void testUpdate_UrlNoScheme() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("ftp.abc.com");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document URL is invalid"));
    }

    @Test
    void testUpdate_UrlInvalid() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("abc");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> courseDocumentService.update(1L, 10L, request));
        assertTrue(ex.getMessage().contains("Document URL is invalid"));
    }

    @Test
    void testUpdate_DescriptionNullOverrides() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("http://abc.com");
        request.setDescription(null);
        document.setDescription("Old Description");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(CourseDocument.class))).thenReturn(document);
        
        CourseDocumentResponse res = courseDocumentService.update(1L, 10L, request);
        assertNull(res.getDescription());
    }

    @Test
    void testUpdate_DescriptionSuccess() {
        CourseDocumentRequest request = new CourseDocumentRequest();
        request.setTitle("Valid");
        request.setDocumentType(DocumentType.PDF);
        request.setUrl("http://abc.com");
        request.setDescription("New Description");
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(CourseDocument.class))).thenReturn(document);
        
        CourseDocumentResponse res = courseDocumentService.update(1L, 10L, request);
        assertEquals("New Description", res.getDescription());
    }
}
