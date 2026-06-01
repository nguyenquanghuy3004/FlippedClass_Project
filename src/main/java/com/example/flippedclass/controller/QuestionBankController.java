package com.example.flippedclass.controller;

import com.example.flippedclass.dto.request.CreateQuestionBankRequest;
import com.example.flippedclass.dto.response.QuestionBankResponse;
import com.example.flippedclass.service.QuestionBankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-bank")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionBankResponse create(@Valid @RequestBody CreateQuestionBankRequest request) {
        return questionBankService.createQuestion(request);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<QuestionBankResponse> createBulk(@Valid @RequestBody List<CreateQuestionBankRequest> requests) {
        return questionBankService.createQuestionsBulk(requests);
    }

    @GetMapping
    public List<QuestionBankResponse> getAll() {
        return questionBankService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public QuestionBankResponse getById(@PathVariable Long id) {
        return questionBankService.getQuestionById(id);
    }

    @PutMapping("/{id}")
    public QuestionBankResponse update(@PathVariable Long id, @Valid @RequestBody CreateQuestionBankRequest request) {
        return questionBankService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        questionBankService.deleteQuestion(id);
    }
}
