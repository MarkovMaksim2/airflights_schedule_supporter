package com.airflights.restrictedzone.exception;

/**
 * Исключение, выбрасываемое при отсутствии ресурса в БД.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
