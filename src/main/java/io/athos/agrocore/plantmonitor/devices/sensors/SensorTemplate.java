package io.athos.agrocore.plantmonitor.devices.sensors;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "name",
        "model",
        "capabilities",
        "defaultParameters",
})
public class SensorTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MeasurementType> capabilities = new HashSet<>();
    @ElementCollection
    private Map<String, String> defaultParameters = new HashMap<>();
    private String model;

    public String getName() {
        if (name == null){
            return "";
        }
        return name;
    }
}
