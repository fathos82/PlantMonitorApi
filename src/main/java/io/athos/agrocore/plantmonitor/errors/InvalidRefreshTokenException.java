package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends DetailErrorException{
    public InvalidRefreshTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
