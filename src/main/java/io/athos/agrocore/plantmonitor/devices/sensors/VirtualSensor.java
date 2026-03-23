package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoring;
import io.athos.agrocore.plantmonitor.devices.Device;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class  VirtualSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Device device;
    private String name; // todo: ver como esse dado sera preenchido.
    private SensorModel model;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MeasurementType> capabilities;
    @ElementCollection
    private Map<String, String> parameters = new HashMap<>();
    @UpdateTimestamp
    private LocalDateTime lastDataAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlantMonitoring plantMonitoring;

    private boolean hasError;
    @Transient
    public boolean isWorking() {
        return lastDataAt != null &&
                lastDataAt.isAfter(LocalDateTime.now().minusSeconds(25)) && !hasError ;
    }

    public void onDataReceived() {
        this.lastDataAt = LocalDateTime.now();
    }




}
