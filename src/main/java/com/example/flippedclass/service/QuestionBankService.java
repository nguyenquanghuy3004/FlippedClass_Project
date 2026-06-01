package com.example.flippedclass.service;

import com.example.flippedclass.dto.request.CreateQuestionBankRequest;
import com.example.flippedclass.dto.response.QuestionBankResponse;

import java.util.List;

public interface QuestionBankService {
    QuestionBankResponse createQuestion(CreateQuestionBankRequest request);
    List<QuestionBankResponse> createQuestionsBulk(List<CreateQuestionBankRequest> requests);
    List<QuestionBankResponse> getAllQuestions();
    QuestionBankResponse getQuestionById(Long id);
    QuestionBankResponse updateQuestion(Long id, CreateQuestionBankRequest request);
    void deleteQuestion(Long id);
}
