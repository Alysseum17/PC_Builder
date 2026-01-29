package com.pcbuilder.core.modules.exception;

public class PasswordMismatchException extends BaseException {
    public PasswordMismatchException(String message) {
        super(message, "PASSWORD_MISMATCH");
    }
}
