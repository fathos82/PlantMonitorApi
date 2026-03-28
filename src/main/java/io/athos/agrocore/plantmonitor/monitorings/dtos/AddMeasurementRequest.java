package io.athos.agrocore.plantmonitor.monitorings.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;

public record AddMeasurementRequest(
        Long plantId,
        MeasurementType measurementType,
        Long sensorId
) {
}
