package com.pcbuilder.core.modules.exception;

public class FileStorageException extends BaseException {
    public FileStorageException(String message) {
        super(message, "FILE_STORAGE_ERROR");
    }
}
