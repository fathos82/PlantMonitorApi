package io.athos.agrocore.plantmonitor.monitorings.measurement.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;

import java.time.Instant;
import java.time.LocalDateTime;

public record ScalarMeasurementValueDTO(
        Long sensorId,
        MeasurementType type,
        Double value,
        Instant measuredAt


        // todo: Ver exatamente como vai funcionar isso? se sera passado do device, se sera do parent...
) implements MeasurementValueDTO
{
}
