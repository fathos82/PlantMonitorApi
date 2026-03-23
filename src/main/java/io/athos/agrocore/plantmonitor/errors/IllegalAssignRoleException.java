package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class IllegalAssignRoleException extends DetailErrorException {
//    public IllegalAssignRoleException(RoleEnum role) {
//        super("The role " + role + " is not allowed to be assigned.", HttpStatus.FORBIDDEN);
//    }

    public IllegalAssignRoleException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
