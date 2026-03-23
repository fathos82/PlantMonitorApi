package io.athos.agrocore.plantmonitor.devices.dtos;

import io.athos.agrocore.plantmonitor.devices.Device;

import java.time.LocalDateTime;

public record DeviceResponse(
        Long id,
        String name,
        Boolean isOnline,
        String hostname,
        String deviceType,
        LocalDateTime firstSeenAt,
        LocalDateTime lastSeenAt){
    public DeviceResponse(Device device) {
        this(device.getId(), device.getName(), device.isOnline(), device.getHostname(), device.getDeviceType(), device.getFirstSeenAt(), device.getLastSeenAt());
    }
}
