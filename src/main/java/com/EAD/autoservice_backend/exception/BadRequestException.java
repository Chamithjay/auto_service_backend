package com.EAD.autoservice_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This tells Spring Boot to send a "400 Bad Request"
// status code whenever this exception is thrown.
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
//When you get an error like "Invalid role specified," it's because the user (the client) sent bad data.
// The server shouldn't crash with a 500 Internal Server Error.
// Instead, it should calmly reply with a 400 Bad Request, telling the client, "You made a mistake."