package io.athos.agrocore.plantmonitor;

import io.athos.agrocore.InvalidEnumValueException;

public interface NormalizedEnum<E extends Enum<E>> {


    static  <T extends Enum<T>> T fromJson(Class<T> enumClass, String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase().replace("-", "_");
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(normalized)) {
                return constant;
            }
        }

        throw new InvalidEnumValueException(enumClass, value);
    }
    default String toJson() {
        String name = ((Enum<?>) this).name()
                .toLowerCase()
                .replace("_per_", "/")
                .replace("_", " ");
        return name;
    }


//    public <T extends Enum<T>> T fromJson(Class<T> enumClass, String value);
}