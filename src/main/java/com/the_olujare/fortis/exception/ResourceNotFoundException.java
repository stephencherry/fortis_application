package com.the_olujare.fortis.exception;

/**
 * Thrown when a requested resource cannot be found.
 * Used to signal missing entities such as users or tasks.
 *
 * This exception is handled globally by GlobalExceptionHandler.
 * Results in an HTTP 404 response being returned to the client.
 *
 * The message provided explains which resource was not found.
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message){
        super(message);
    }
}