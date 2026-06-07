package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.request.CreateQuestionBankRequest;
import com.example.flippedclass.dto.response.QuestionBankResponse;
import com.example.flippedclass.entity.QuestionBank;
import com.example.flippedclass.repository.QuestionBankRepository;
import com.example.flippedclass.service.QuestionBankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;

    public QuestionBankServiceImpl(QuestionBankRepository questionBankRepository) {
        this.questionBankRepository = questionBankRepository;
    }

    @Override
    @Transactional
    public QuestionBankResponse createQuestion(CreateQuestionBankRequest request) {
        QuestionBank qb = QuestionBank.builder()
                .content(request.getContent())
                .options(request.getOptions())
                .correctAnswer(request.getCorrectAnswer())
                .points(request.getPoints() != null ? request.getPoints() : 10)
                .questionType(request.getQuestionType() != null ? request.getQuestionType() : "SINGLE_CHOICE")
                .explanation(request.getExplanation())
                .build();
        qb = questionBankRepository.save(qb);
        return mapToResponse(qb);
    }

    @Override
    @Transactional
    public List<QuestionBankResponse> createQuestionsBulk(List<CreateQuestionBankRequest> requests) {
        List<QuestionBank> questions = requests.stream().map(req -> QuestionBank.builder()
                .content(req.getContent())
                .options(req.getOptions())
                .correctAnswer(req.getCorrectAnswer())
                .points(req.getPoints() != null ? req.getPoints() : 10)
                .questionType(req.getQuestionType() != null ? req.getQuestionType() : "SINGLE_CHOICE")
                .explanation(req.getExplanation())
                .build()).collect(Collectors.toList());

        return questionBankRepository.saveAll(questions).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionBankResponse> getAllQuestions() {
        return questionBankRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionBankResponse getQuestionById(Long id) {
        QuestionBank qb = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found in bank"));
        return mapToResponse(qb);
    }

    @Override
    @Transactional
    public QuestionBankResponse updateQuestion(Long id, CreateQuestionBankRequest request) {
        QuestionBank qb = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found in bank"));
        qb.setContent(request.getContent());
        qb.setOptions(request.getOptions());
        qb.setCorrectAnswer(request.getCorrectAnswer());
        if(request.getPoints() != null) qb.setPoints(request.getPoints());
        if(request.getQuestionType() != null) qb.setQuestionType(request.getQuestionType());
        qb.setExplanation(request.getExplanation());
        return mapToResponse(questionBankRepository.save(qb));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionBankRepository.deleteById(id);
    }

    private QuestionBankResponse mapToResponse(QuestionBank qb) {
        return QuestionBankResponse.builder()
                .id(qb.getId())
                .content(qb.getContent())
                .options(qb.getOptions())
                .correctAnswer(qb.getCorrectAnswer())
                .points(qb.getPoints())
                .questionType(qb.getQuestionType())
                .explanation(qb.getExplanation())
                .build();
    }
}
