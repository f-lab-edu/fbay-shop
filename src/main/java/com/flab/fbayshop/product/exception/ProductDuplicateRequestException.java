package com.flab.fbayshop.product.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class ProductDuplicateRequestException extends BusinessException {

    public ProductDuplicateRequestException() {
        super(PRODUCT_DUPLICATED_REQUEST);
    }
}
