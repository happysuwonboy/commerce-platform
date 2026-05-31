package com.hsboy.commerce.common.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        int status,
        String code,
        String message,
        List<FieldError> errors,
        LocalDateTime timestamp,
        String path
) {

    public record FieldError(String field, String message) {}

    public static ApiError of(ErrorCode errorCode, String path) {
        return new ApiError(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                null,
                LocalDateTime.now(),
                path
        );
    }

    public static ApiError of(ErrorCode errorCode, String message, String path) {
        return new ApiError(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message,
                null,
                LocalDateTime.now(),
                path
        );
    }

    public static ApiError ofValidation(List<FieldError> errors, String path) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.INVALID_INPUT.getCode(),
                ErrorCode.INVALID_INPUT.getDefaultMessage(),
                errors,
                LocalDateTime.now(),
                path
        );
    }


}
