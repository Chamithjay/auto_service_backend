package com.EAD.autoservice_backend.exception;

public class ServiceItemNotFoundException extends RuntimeException{
    public ServiceItemNotFoundException(String s) {
        super(s);
    }
}
