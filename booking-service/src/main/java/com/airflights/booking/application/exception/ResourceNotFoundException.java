package com.airflights.booking.application.exception;

/**
 * Исключение, выбрасываемое при отсутствии ресурса в БД.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
