package com.flab.fbayshop.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    public CustomAuthenticationException(String message) {
        super(message);
    }
}
