package io.athos.agrocore.plantmonitor.monitorings.measurement;

import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementValueDTO;
import io.athos.agrocore.plantmonitor.monitorings.measurement.entities.MeasurementValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MeasurementValueAndDtoPair  {
    public MeasurementValue measurementValue;
    public MeasurementValueDTO measurementDTO;
}
