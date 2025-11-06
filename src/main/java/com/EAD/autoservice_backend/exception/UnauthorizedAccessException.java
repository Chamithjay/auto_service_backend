package com.EAD.autoservice_backend.exception;

/**
 * Custom exception for unauthorized access attempts
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}