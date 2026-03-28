package io.athos.agrocore.plantmonitor.monitorings.measurement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Setter

@Table(
        indexes = {
                @Index(
                        name = "idx_measurement_value_parent_timestamp",
                        columnList = "measurement_parent_id, timestamp"
                )
        }
)
public  class MeasurementValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_parent_id", nullable = false)
    @JsonBackReference
    private Measurement measurementParent;
    @Getter
    @Column(nullable = false)
    private Instant timestamp;
    @Getter
    @Column(nullable = false)
    private float value;
}
