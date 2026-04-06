package io.athos.agrocore.plantmonitor.monitorings.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdatePlantMonitoringRequest(
        String commonName,
        String specieName
        // todo: Tipos de monitoramesnto
) {
}
