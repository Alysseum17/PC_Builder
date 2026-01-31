package com.pcbuilder.core.modules.exception;

public class ImageProcessingException extends BaseException {
    public ImageProcessingException(String message) {
        super(message, "IMAGE_PROCESSING_ERROR");
    }
}
