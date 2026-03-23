package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class DeleteUserException extends DetailErrorException{
    public DeleteUserException() {
        super("Você não pode deletar sua conta tendo clientes associados", HttpStatus.BAD_REQUEST);
    }
}
