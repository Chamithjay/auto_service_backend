package com.EAD.autoservice_backend.exception;

/**
 * Custom exception for when a requested resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}