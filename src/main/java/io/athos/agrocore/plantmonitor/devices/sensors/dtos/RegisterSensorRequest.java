package io.athos.agrocore.plantmonitor.devices.sensors.dtos;

import io.athos.agrocore.plantmonitor.devices.sensors.SensorModel;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;

import java.util.List;
import java.util.Map;

public record RegisterSensorRequest(
        String deviceUid, // todo: change to the normal ID
        String sensorName, // todo: apelido ou modelo?
//        List<MeasurementType> capabilities,
        Map<String, String>parameters,
        SensorModel model
) {

}
