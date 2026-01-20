package com.airflights.passenger.application.exception;

/**
 * Исключение, выбрасываемое при отсутствии прав на действие.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
