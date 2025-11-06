package com.EAD.autoservice_backend.exception;

public class AppointmentJobNotFoundException extends RuntimeException{
    public AppointmentJobNotFoundException(String s) {
        super(s);
    }
}
