package io.athos.agrocore.plantmonitor.monitorings.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import jakarta.validation.constraints.NotNull;

public record AddMeasurementRequest(
        @NotNull
        Long plantId,
        @NotNull
        MeasurementType measurementType,
        @NotNull
        Long sensorId
) {
}
