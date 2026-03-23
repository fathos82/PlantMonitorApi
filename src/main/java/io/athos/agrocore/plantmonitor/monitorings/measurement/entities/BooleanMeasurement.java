package io.athos.agrocore.plantmonitor.monitorings.measurement.entities;

public class BooleanMeasurement extends MeasurementValue {
    private Boolean value;
    private String name;



    public enum BooleanSensorType {
        DOOR_OPEN,
        MOTION_DETECTED,
        MOTOR_ON,
        IRRIGATION_ACTIVE
    }

}
