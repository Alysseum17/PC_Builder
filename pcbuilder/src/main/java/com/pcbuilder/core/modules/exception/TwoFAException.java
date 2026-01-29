package com.pcbuilder.core.modules.exception;

public class TwoFAException extends BaseException {
    public TwoFAException(String message) {
        super(message, "TWO_FA_ERROR");
    }
}
