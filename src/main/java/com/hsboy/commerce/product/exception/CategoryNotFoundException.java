package com.hsboy.commerce.product.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(Long id) {
        super(ErrorCode.CATEGORY_NOT_FOUND, "존재하지 않는 카테고리 : " + id);
    }
}
