package com.hsboy.commerce.user.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class NotExistedUserException extends BusinessException {

    public NotExistedUserException(String email) {
        super(ErrorCode.USER_NOT_FOUND, "존재하지 않는 유저입니다: " + email);
    }
}
