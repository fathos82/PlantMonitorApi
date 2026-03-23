package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class InvalidInputRefreshToken extends DetailErrorException {
    public InvalidInputRefreshToken(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
    public InvalidInputRefreshToken() {
        super("O token informado é um AccessToken, mas esperava-se um RefreshToken.", HttpStatus.BAD_REQUEST);
    }
}

