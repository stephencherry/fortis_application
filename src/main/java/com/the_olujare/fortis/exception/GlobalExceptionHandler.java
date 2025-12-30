package com.the_olujare.fortis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling for the entire application.
 * Converts server-side exceptions into consistent HTTP responses.
 *
 * ResourceNotFoundException
 *  - Returned when a requested entity cannot be found.
 *  - Responds with HTTP 404.
 *
 * RuntimeException
 *  - Catches business and validation errors.
 *  - Responds with HTTP 400 to indicate a bad request.
 *
 * Exception
 *  - Fallback handler for all unexpected errors.
 *  - Responds with HTTP 500 without exposing internal details.
 *
 * buildErrorResponse()
 *  - Creates a uniform error payload.
 *  - Includes error message, HTTP status code, and timestamp.
 *
 * This approach keeps controllers clean.
 * It ensures clients receive predictable and readable error responses.
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException resourceNotFoundException) {
        return buildErrorResponse(resourceNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException runtimeException) {
        return buildErrorResponse(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception exception) {
        return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        body.put("status", status.value());
        body.put("timestamp", java.time.Instant.now());
        return new ResponseEntity<>(body, status);
    }
}