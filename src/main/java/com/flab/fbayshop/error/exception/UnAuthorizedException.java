package com.flab.fbayshop.error.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

public class UnAuthorizedException extends BusinessException {

    public UnAuthorizedException() {
        super(UNAUTHORIZED);
    }

}
