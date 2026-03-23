package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class InvalidBearerTokenException extends DetailErrorException {

    public InvalidBearerTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
