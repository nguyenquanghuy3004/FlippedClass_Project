package controller;

import dto.response.*;
import dto.request.CreateQuizQuestionRequest;
import dto.request.CreateQuizRequest;
import dto.request.SubmitQuizAttemptRequest;
import dto.request.UpdateQuizRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.QuizService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    //ssss

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse create(@Valid @RequestBody CreateQuizRequest request) {
        return quizService.create(request);
    }

    @PutMapping("/{id}")
    public QuizResponse update(@PathVariable("id") @Positive(message = "id must be a positive number") Long id,
                               @Valid @RequestBody UpdateQuizRequest request) {
        return quizService.update(id, request);
    }

    @GetMapping("/{id}")
    public QuizResponse getById(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        return quizService.getById(id);
    }

    @GetMapping
    public List<QuizResponse> getAll() {
        return quizService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Positive(message = "id must be a positive number") Long id) {
        quizService.delete(id);
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizQuestionResponse addQuestion(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                                            @Valid @RequestBody CreateQuizQuestionRequest request) {
        return quizService.addQuestion(quizId, request);
    }

    @GetMapping("/{quizId}/questions")
    public List<QuizQuestionResponse> getQuestions(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getQuestions(quizId);
    }

    @DeleteMapping("/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable("questionId") @Positive(message = "questionId must be a positive number") Long questionId) {
        quizService.deleteQuestion(questionId);
    }

    @PostMapping("/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptResponse submitAttempt(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId,
                                             @Valid @RequestBody SubmitQuizAttemptRequest request) {
        return quizService.submitAttempt(quizId, request);
    }

    @GetMapping("/{quizId}/attempts")
    public List<QuizAttemptResponse> getAttempts(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getAttempts(quizId);
    }

    @GetMapping("/{quizId}/statistics")
    public QuizStatisticsResponse getStatistics(@PathVariable("quizId") @Positive(message = "quizId must be a positive number") Long quizId) {
        return quizService.getStatistics(quizId);
    }
}