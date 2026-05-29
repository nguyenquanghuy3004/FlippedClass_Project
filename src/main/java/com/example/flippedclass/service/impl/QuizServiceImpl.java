package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.response.QuizResponse;
import com.example.flippedclass.repository.NodeRepository;
import com.example.flippedclass.repository.QuizRepository;
import com.example.flippedclass.service.QuizService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final NodeRepository nodeRepository;

    public QuizServiceImpl(QuizRepository quizRepository, NodeRepository nodeRepository) {
        this.quizRepository = quizRepository;
        this.nodeRepository = nodeRepository;
    }

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
