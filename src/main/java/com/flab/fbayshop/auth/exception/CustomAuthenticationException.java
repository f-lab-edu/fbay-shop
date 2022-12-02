package com.flab.fbayshop.auth.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    public CustomAuthenticationException() {
        super(USER_LOGIN_FAIL.getMessage());
    }

}
