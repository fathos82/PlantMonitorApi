package io.athos.agrocore.plantmonitor.monitorings.measurement.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;

import java.time.Instant;
import java.time.LocalDateTime;

public interface MeasurementValueDTO {
    Long sensorId();
    MeasurementType type();
    Instant measuredAt();
}
