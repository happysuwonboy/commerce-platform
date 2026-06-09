package com.hsboy.commerce.product.exception;

import com.hsboy.commerce.common.exception.BusinessException;
import com.hsboy.commerce.common.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long id) {
        super(ErrorCode.PRODUCT_NOT_FOUND, "존재하지 않는 상품 : " + id);
    }
}
