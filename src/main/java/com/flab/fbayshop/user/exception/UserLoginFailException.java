package com.flab.fbayshop.user.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class UserLoginFailException extends BusinessException {

    public UserLoginFailException() {
        super(USER_LOGIN_FAIL);
    }

    public UserLoginFailException(String message) {
        super(message, USER_LOGIN_FAIL);
    }
}
