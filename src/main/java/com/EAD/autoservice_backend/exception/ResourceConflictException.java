package com.EAD.autoservice_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This tells Spring to automatically send a 409 CONFLICT error
// when this exception is thrown.
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}