package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.devices.Device;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoring;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VirtualSensor {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private PlantMonitoring plantMonitoring;
    @ManyToOne
    private SensorTemplate sensorTemplate;
    @UpdateTimestamp
    private LocalDateTime lastDataAt;
    @ElementCollection
    private Map<String, String> parameters = new HashMap<>();
    private boolean hasError;
    @Transient
    public boolean isWorking() {
        return lastDataAt != null &&
                lastDataAt.isAfter(LocalDateTime.now().minusSeconds(5)) && !hasError ;
    }
    public void onDataReceived() {
        hasError = false;
        this.lastDataAt = LocalDateTime.now();
    }
    public VirtualSensor(SensorTemplate sensorTemplate, Device device, Map<String, String> parameters){
        this.device = device;
        this.sensorTemplate = sensorTemplate;
        this.parameters = parameters;
        if (parameters == null || parameters.isEmpty()) {
            this.parameters = new HashMap<>(sensorTemplate.getDefaultParameters());
        }
    }
}
