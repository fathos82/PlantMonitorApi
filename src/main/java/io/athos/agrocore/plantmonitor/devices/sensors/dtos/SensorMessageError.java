package io.athos.agrocore.plantmonitor.devices.sensors.dtos;

import io.athos.agrocore.plantmonitor.devices.sensors.SensorNotify;

import java.time.LocalDateTime;

public record SensorMessageError(
        String message,
        Long sensorId,
        LocalDateTime dateTime
) {
    public SensorMessageError(SensorNotify sensorNotify) {
        this(sensorNotify.getMessage(), sensorNotify.getSensor().getId(), sensorNotify.getCreatedAt());
    }

}
