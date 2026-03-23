package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class ResetPasswordException extends DetailErrorException {
    public ResetPasswordException() {
        super("A nova senha não pode ser igual à senha atual.", HttpStatus.BAD_REQUEST);
    }
}
