package com.EAD.autoservice_backend.exception;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String s) {
        super(s);
    }

}
