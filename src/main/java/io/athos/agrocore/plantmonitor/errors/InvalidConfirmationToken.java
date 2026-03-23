package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class InvalidConfirmationToken extends DetailErrorException {
    public InvalidConfirmationToken() {
        super("Token de confirmação invalido ou expirado!", HttpStatus.UNAUTHORIZED);
    }
}
