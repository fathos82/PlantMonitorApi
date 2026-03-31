package io.athos.agrocore.plantmonitor.monitorings.dtos;

import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoring;
import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import io.athos.agrocore.plantmonitor.users.User;
import jakarta.persistence.*;

import java.util.List;

public record PlantMonitoringResponse(
        Long id,
        Long userId,
        String commonName,
        String specieName,
        List<MeasurementResponse> measurements // TODO: A Decidir se mantemos!

)
{
    public PlantMonitoringResponse(PlantMonitoring plantMonitoring){
        this(
                plantMonitoring.getId(),
                plantMonitoring.getUser().getId(),
                plantMonitoring.getCommonName(),
                plantMonitoring.getSpecieName(),
                plantMonitoring.getMeasurements().stream().map(MeasurementResponse::new).toList()
        );

    }
}
