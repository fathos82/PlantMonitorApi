package io.athos.agrocore.plantmonitor.monitorings.dtos;

import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePlantMonitoringRequest(
        @NotNull
        Long userId,
        String commonName,
        String specieName
        // todo: Tipos de monitoramesnto
        ) {
}
