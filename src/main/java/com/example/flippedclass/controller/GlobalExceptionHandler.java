package com.example.flippedclass.controller;

import com.example.flippedclass.dto.response.ApiErrorResponse;
import com.example.flippedclass.exception.BusinessException;
import com.example.flippedclass.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.builder()
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBusiness(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(Exception ex) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("BAD_CREDENTIALS")
                .message("Tài khoản hoặc mật khẩu không chính xác.")
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("VALIDATION_FAILED")
                .message("Request body validation failed")
                .fieldErrors(mapFieldErrors(ex.getBindingResult().getFieldErrors()))
                .build());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        List<ApiErrorResponse.FieldErrorDetail> details = new ArrayList<>();
        ex.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> details.add(ApiErrorResponse.FieldErrorDetail.builder()
                        .field(result.getMethodParameter().getParameterName())
                        .message(error.getDefaultMessage())
                        .build())));
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("VALIDATION_FAILED")
                .message("Request parameter validation failed")
                .fieldErrors(details)
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiErrorResponse.FieldErrorDetail> details = ex.getConstraintViolations().stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("VALIDATION_FAILED")
                .message("Request parameter validation failed")
                .fieldErrors(details)
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .error("MALFORMED_REQUEST")
                .message("Malformed JSON request body")
                .build());
    }

    private List<ApiErrorResponse.FieldErrorDetail> mapFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(error -> ApiErrorResponse.FieldErrorDetail.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue() != null ? String.valueOf(error.getRejectedValue()) : null)
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
    }

    private ApiErrorResponse.FieldErrorDetail mapConstraintViolation(ConstraintViolation<?> violation) {
        return ApiErrorResponse.FieldErrorDetail.builder()
                .field(violation.getPropertyPath().toString())
                .rejectedValue(violation.getInvalidValue() != null ? String.valueOf(violation.getInvalidValue()) : null)
                .message(violation.getMessage())
                .build();
    }
}
