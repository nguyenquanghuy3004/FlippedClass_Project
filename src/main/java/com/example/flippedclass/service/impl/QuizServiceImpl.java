package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.repository.NodeRepository;
import com.example.flippedclass.repository.QuizRepository;
import com.example.flippedclass.service.QuizService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// Xử lý nghiệp vụ lấy quiz theo node học tập
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final NodeRepository nodeRepository;

    // Lấy danh sách quiz sau khi kiểm tra node tồn tại
    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> findByLearningNode(Long nodeId) {
        if (!nodeRepository.existsById(nodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning node not found");
        }

        return quizRepository.findByLearningNode_IdOrderByCreatedAtDesc(nodeId).stream()
                .map(QuizResponse::from)
                .toList();
    }
}
