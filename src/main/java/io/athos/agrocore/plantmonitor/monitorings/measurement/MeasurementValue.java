package io.athos.agrocore.plantmonitor.monitorings.measurement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


// TODO:
//@SequenceGenerator(
//        name = "measurement_value_seq",
//        sequenceName = "measurement_value_seq",
//        allocationSize = 50 // 🔥 importante para batch insert
//)
//@GeneratedValue(
//        strategy = GenerationType.SEQUENCE,
//        generator = "measurement_value_seq"
//)
// PARTITION BY RANGE (timestamp)


@Entity
@Table(
        name = "measurement_value",
        indexes = {
                @Index(
                        name = "idx_measurement_parent_time",
                        columnList = "measurement_parent_id, timestamp"
                )

        }
)
@Setter
@Getter
@NoArgsConstructor
public  class MeasurementValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "measurement_parent_id", nullable = false)
    @JsonBackReference
    private Measurement measurementParent;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private double value;

    public MeasurementValue(Measurement measurementParent, Instant timestamp, double value) {
        this.measurementParent = measurementParent;
        this.timestamp = timestamp;
        this.value = value;
    }

}
