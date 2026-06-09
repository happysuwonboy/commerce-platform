package com.hsboy.commerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_INVALID_INPUT", "유효하지 않은 입력값입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_INTERNAL_ERROR", "알 수 없는 에러가 발생하였습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_DUPLICATE_EMAIL", "중복된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "올바르지 않은 비밀번호입니다"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND", "리프레쉬 토큰 정보를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

}
