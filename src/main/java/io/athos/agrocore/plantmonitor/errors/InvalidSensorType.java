package io.athos.agrocore.plantmonitor.errors;

public class InvalidSensorType extends RuntimeException {
    public InvalidSensorType() {
        super("Invalid sensor type");
    }

}
