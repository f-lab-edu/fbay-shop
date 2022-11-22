package com.flab.fbayshop.error.exception;

import com.flab.fbayshop.error.dto.ErrorType;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorType errorType;

    public BusinessException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public BusinessException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

}
