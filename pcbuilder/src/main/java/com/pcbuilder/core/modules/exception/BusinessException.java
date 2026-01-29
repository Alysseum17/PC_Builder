package com.pcbuilder.core.modules.exception;

public class BusinessException extends BaseException {

    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }

}