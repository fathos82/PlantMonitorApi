package io.athos.agrocore.plantmonitor.devices.sensors.dtos;


import java.util.Map;

public record UpdateSensorRequest(
//        String name,// todo: apelido ou modelo?
        Map<String, String> parameters

) {
        }
