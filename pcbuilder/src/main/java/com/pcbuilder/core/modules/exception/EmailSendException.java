package com.pcbuilder.core.modules.exception;

public class EmailSendException extends BaseException {
    public EmailSendException(String message) {
      super(message, "EMAIL_SEND_ERROR");
    }
}
