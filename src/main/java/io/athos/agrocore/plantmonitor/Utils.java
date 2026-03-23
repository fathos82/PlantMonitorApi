package io.athos.agrocore.plantmonitor;

import java.util.function.Consumer;

public class Utils {

    public static <T> void saveIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
