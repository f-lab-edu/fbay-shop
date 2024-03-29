package com.flab.fbayshop.user.exception;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import com.flab.fbayshop.error.exception.BusinessException;

public class AddressNotFoundException extends BusinessException {

    public AddressNotFoundException() {
        super(ADDRESS_NOT_FOUND);
    }
}
