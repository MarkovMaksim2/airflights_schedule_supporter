package com.airflights.file.application.exception;

public class FileUploadFailedException extends RuntimeException {
    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
