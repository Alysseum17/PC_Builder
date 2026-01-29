package com.pcbuilder.core.modules.exception;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
    }

}