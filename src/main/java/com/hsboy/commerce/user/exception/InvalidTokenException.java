package com.hsboy.commerce.user.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다");
    }
}
