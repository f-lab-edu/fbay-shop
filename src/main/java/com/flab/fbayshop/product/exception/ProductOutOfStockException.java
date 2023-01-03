package com.flab.fbayshop.product.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class ProductOutOfStockException extends BusinessException {
    public ProductOutOfStockException() {
        super(PRODUCT_OUT_OF_STOCK);
    }
}
