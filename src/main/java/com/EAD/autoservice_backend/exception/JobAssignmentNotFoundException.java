package com.EAD.autoservice_backend.exception;

public class JobAssignmentNotFoundException extends RuntimeException {
    public JobAssignmentNotFoundException(String jobAssignmentNotFound) {
        super(jobAssignmentNotFound);
    }
}
