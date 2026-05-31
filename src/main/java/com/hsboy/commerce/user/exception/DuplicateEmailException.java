package com.hsboy.commerce.user.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다: " + email);
    }
}
