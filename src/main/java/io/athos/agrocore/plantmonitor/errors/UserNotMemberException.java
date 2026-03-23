package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class UserNotMemberException extends DetailErrorException {
    public UserNotMemberException() {
        super("O Usuario não é membro do projeto.", HttpStatus.FORBIDDEN);
    }

    public UserNotMemberException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
