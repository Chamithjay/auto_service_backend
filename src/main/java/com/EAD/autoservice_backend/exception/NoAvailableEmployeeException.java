package com.EAD.autoservice_backend.exception;

public class NoAvailableEmployeeException extends RuntimeException {
    public NoAvailableEmployeeException(String message) {
        super(message);
    }
}
