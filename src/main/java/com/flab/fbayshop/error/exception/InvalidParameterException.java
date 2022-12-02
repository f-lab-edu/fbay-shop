package com.flab.fbayshop.error.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

public class InvalidParameterException extends BusinessException {

    public InvalidParameterException() {
        super(INVALID_PARAMETER);
    }

    public InvalidParameterException(String message) {
        super(message, INVALID_PARAMETER);
    }
}
