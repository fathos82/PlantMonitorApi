package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoring;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
public class SensorTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MeasurementType> capabilities;
    @ElementCollection
    private Map<String, String> defaultParameters = new HashMap<>();
    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlantMonitoring plantMonitoring;



}
