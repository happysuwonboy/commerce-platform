package com.hsboy.commerce.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 핸들러 ① — @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        List<ApiError.FieldError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(),
                        fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiError.ofValidation(errors, request.getRequestURI()));
    }

    // 핸들러 ② — 비즈니스 예외 (DuplicateEmailException 등 BusinessException 자식 전부)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException e,
            HttpServletRequest request) {

        ErrorCode ec = e.getErrorCode();
        log.warn("Business exception: {}", e.getMessage());

        return ResponseEntity.status(ec.getStatus())
                .body(ApiError.of(ec, e.getMessage(), request.getRequestURI()));
    }

    // 핸들러 ③ — 예상 못 한 시스템 에러 (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(
            Exception e,
            HttpServletRequest request) {

        log.error("Unexpected error", e);

        return ResponseEntity.internalServerError()
                .body(ApiError.of(ErrorCode.INTERNAL_ERROR, request.getRequestURI()));
    }
}
