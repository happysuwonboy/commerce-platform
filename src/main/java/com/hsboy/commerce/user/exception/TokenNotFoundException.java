package com.hsboy.commerce.user.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(ErrorCode.TOKEN_NOT_FOUND, "토큰 정보가 없습니다");
    }
}
