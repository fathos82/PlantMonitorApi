package io.athos.agrocore.plantmonitor.monitorings.measurement.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@DiscriminatorValue("SCALAR")
public class ScalarMeasurementValue extends MeasurementValue {
    @Getter
    @Column(nullable = false)
    private Double value;

    private String unit; // todo: change to enum
}

