package com.flab.fbayshop.product.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class ProductNotSaleException extends BusinessException {
    public ProductNotSaleException() {
        super(PRODUCT_NOT_SALE);
    }
}
