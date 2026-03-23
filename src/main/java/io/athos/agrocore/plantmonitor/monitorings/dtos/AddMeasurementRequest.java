package io.athos.agrocore.plantmonitor.monitorings.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;

public record AddMeasurementRequest(
        MeasurementType measurementType,
        Long sensorId
) {
}
