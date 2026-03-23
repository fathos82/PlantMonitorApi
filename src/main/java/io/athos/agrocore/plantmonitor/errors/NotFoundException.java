package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class NotFoundException extends DetailErrorException {
    public NotFoundException(String EntityName, Long id) {
        super(EntityName + " not found with id " + id, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String EntityName, String queryParamName, String param) {
        super(EntityName+ " not found with " + queryParamName + ": " + param, HttpStatus.NOT_FOUND);
    }
    public  NotFoundException(Class<?> EntityClass, Long id) {
        this(EntityClass, "id", id.toString());
    }

    public NotFoundException(Class<?>  EntityClass, String queryParamName, Object param) {
        super(EntityClass.getSimpleName() + " not found with " + queryParamName + ": " + param.toString(), HttpStatus.NOT_FOUND);
    }

}
