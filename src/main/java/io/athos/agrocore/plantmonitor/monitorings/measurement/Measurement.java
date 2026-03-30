package io.athos.agrocore.plantmonitor.monitorings.measurement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoring;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sensor_measurement_type",
                        columnNames = {"virtual_sensor_id", "measurement_type"}
                ),

                @UniqueConstraint(
                        name = "uk_plant_monitoring_measurement_type",
                        columnNames = {"plant_monitoring_id", "measurement_type"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_measurement_plant",
                        columnList = "plant_monitoring_id"
                ),
                @Index(
                        name = "idx_measurement_sensor_type",
                        columnList = "virtual_sensor_id, measurement_type"
                ),
                @Index(
                        name = "idx_measurement_type_plant",
                        columnList = "plant_monitoring_id, measurement_type"
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_sensor_id", nullable = false)
    private VirtualSensor virtualSensor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementType measurementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_monitoring_id", nullable = false)
    private PlantMonitoring plantMonitoring;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measurementParent", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MeasurementValue> values = new ArrayList<>();
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public MeasurementValue addMeasurementValue(MeasurementValue valueMeasurement) {
        valueMeasurement.setMeasurementParent(this);
        return valueMeasurement;
    }

}
