package com.flab.fbayshop.common.validate;

public class ValidationPattern {

    public static final String EMAIL_PATTERN = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$";

}
