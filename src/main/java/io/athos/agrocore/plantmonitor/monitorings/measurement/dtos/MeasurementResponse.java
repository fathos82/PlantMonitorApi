package io.athos.agrocore.plantmonitor.monitorings.measurement.dtos;
import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementValue;

import java.time.LocalDateTime;
import java.util.List;

public record MeasurementResponse(
        Long id,
        Long virtualSensorId,

        MeasurementType measurementType,

        Long plantMonitoringId,

        List<MeasurementValue> values,

        LocalDateTime updatedAt
) {
    public MeasurementResponse(Measurement measurement) {
        this(measurement.getId(), measurement.getVirtualSensor().getId(), measurement.getMeasurementType(), measurement.getPlantMonitoring().getId(), measurement.getValues(), measurement.getUpdatedAt());
    }
}
