package io.athos.agrocore.plantmonitor.errors;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class InvalidEnumValueException extends RuntimeException {
    private final String invalidValue;
    private final List<String> validValues;
    private final String fieldName;

    public InvalidEnumValueException(Class<? extends Enum<?>> enumClass, String invalidValue) {
        super("Invalid value for field '" + toCamelCase(enumClass.getSimpleName()) + "': " + invalidValue);
        this.fieldName = toCamelCase(enumClass.getSimpleName());
        this.invalidValue = invalidValue;
        this.validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .toList();
    }


    private static String toCamelCase(String input) {
        if (input == null) return null;
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
}
