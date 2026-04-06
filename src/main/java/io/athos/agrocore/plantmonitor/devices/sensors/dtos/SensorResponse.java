package io.athos.agrocore.plantmonitor.devices.sensors.dtos;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import java.util.Map;
import java.util.Set;

public record SensorResponse(
        Long id,
        Long deviceId,
        String name,
        String alias,
        String model,

        Set<MeasurementType> capabilities,
        boolean isWorking,
        // models
        // all information
        Map<String, String> parameters) {
    public SensorResponse(VirtualSensor sensor) {
        this(sensor.getId(), sensor.getDevice().getId(), sensor.getSensorTemplate().getName(),sensor.getAlias(), sensor.getSensorTemplate().getModel(), sensor.getSensorTemplate().getCapabilities(), sensor.isWorking(), sensor.getParameters());
    }
}
