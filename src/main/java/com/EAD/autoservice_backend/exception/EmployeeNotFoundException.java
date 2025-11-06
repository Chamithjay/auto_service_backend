package com.EAD.autoservice_backend.exception;

public class EmployeeNotFoundException extends RuntimeException{
    public EmployeeNotFoundException(String s) {
        super(s);
    }
}
