package io.athos.agrocore.plantmonitor.devices.sensors.dtos;


import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record RegisterSensorRequest(
        String deviceUid, // todo: change to the normal ID

        Map<String, String>parameters,
        @NotNull
        Long sensorTemplateId
) {

}
