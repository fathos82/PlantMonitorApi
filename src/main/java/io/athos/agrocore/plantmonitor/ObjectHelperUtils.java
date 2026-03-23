package io.athos.agrocore.plantmonitor;

import java.util.function.Consumer;

public class ObjectHelperUtils {
    public static <T> void  setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }
}
