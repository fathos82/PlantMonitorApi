package io.athos.agrocore.plantmonitor.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DetailErrorException extends RuntimeException {
    private HttpStatus status;


    public DetailErrorException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
