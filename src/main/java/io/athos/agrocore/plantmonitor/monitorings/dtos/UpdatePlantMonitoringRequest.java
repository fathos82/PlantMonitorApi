package io.athos.agrocore.plantmonitor.monitorings.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdatePlantMonitoringRequest(
        @NotNull
        Long userId,
        String commonName,
        String specieName
        // todo: Tipos de monitoramesnto
) {
}
