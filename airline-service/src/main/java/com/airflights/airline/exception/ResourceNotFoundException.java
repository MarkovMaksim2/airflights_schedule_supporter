package com.airflights.airline.exception;

/**
 * Исключение, выбрасываемое при отсутствии ресурса в БД.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
