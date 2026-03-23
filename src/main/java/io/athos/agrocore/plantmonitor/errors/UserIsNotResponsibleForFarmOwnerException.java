package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class UserIsNotResponsibleForFarmOwnerException extends DetailErrorException {
    public UserIsNotResponsibleForFarmOwnerException(Long userId, Long farmOwnerId) {
        super(
                String.format(
                        "O usuário autenticado não é responsável pelo proprietário de fazendas com ID %d.",
                        userId,
                        farmOwnerId
                ),
                HttpStatus.FORBIDDEN
        );
    }
}
