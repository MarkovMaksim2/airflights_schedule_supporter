package com.airflights.auth.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
    private static final String STATUS_ENUM = "status";
    private static final String TIMESTAMP_ENUM = "timestamp";
    private static final String ERROR_ENUM = "error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP_ENUM, LocalDateTime.now());
        body.put(STATUS_ENUM, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR_ENUM, "Validation failed");
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex) {
        log.error("Auth error: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(UsernameNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP_ENUM, LocalDateTime.now());
        body.put(STATUS_ENUM, status.value());
        body.put(ERROR_ENUM, status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP_ENUM, LocalDateTime.now());
        body.put(STATUS_ENUM, status.value());
        body.put(ERROR_ENUM, status.getReasonPhrase());
        return new ResponseEntity<>(body, status);
    }
}
