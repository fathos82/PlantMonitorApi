package io.athos.agrocore.plantmonitor.devices.dtos;

import jakarta.validation.constraints.NotNull;

public record SetUserToDeviceRequest (
    @NotNull
    String deviceUuid
    ){
}
