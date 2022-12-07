package com.flab.fbayshop.user.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class UserDuplicatedException extends BusinessException {

    public UserDuplicatedException() {
        super(USER_DUPLICATED);
    }

}
