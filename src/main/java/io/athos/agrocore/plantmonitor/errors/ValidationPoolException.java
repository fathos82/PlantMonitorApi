package io.athos.agrocore.plantmonitor.errors;

import java.util.HashMap;
import java.util.Map;

public class ValidationPoolException extends RuntimeException {

    private final Map<String, String> errors = new HashMap<>();

    public ValidationPoolException(String field, String message) {
        super("Validation failed");
        this.errors.put(field, message);
    }

    public ValidationPoolException(Map<String, String> errors) {
        super("Validation failed");
        if (errors != null) {
            this.errors.putAll(errors);
        }
    }

    public ValidationPoolException() {
        super("Validation failed");
    }

    public void addError(String field, String message) {
        this.errors.put(field, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
