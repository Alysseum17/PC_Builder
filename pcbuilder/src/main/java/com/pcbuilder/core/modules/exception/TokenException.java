package com.pcbuilder.core.modules.exception;

public class TokenException extends BaseException {
    public TokenException(String message) {
        super(message, "TOKEN_ERROR");
    }
}
