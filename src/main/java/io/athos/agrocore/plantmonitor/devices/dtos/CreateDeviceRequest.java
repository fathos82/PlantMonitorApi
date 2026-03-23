package io.athos.agrocore.plantmonitor.devices.dtos;

public record CreateDeviceRequest(
        String name,
        String hostName,
        String deviceUid,
        String deviceType
        // tokenUser
) {
}
