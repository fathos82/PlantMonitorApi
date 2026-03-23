package io.athos.agrocore.plantmonitor.devices.dtos;

public record UpdateDeviceRequest(
        String name,
        String hostName,
        String deviceUid,
        String deviceType
        // tokenUser
) {
}
