package com.flab.fbayshop.user.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class UserProcessException extends BusinessException {

    public UserProcessException() {
        super(USER_PROCESS_FAIL);
    }

    public UserProcessException(String message) {
        super(message, USER_PROCESS_FAIL);
    }
}
