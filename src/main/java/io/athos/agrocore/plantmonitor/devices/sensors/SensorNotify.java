package io.athos.agrocore.plantmonitor.devices.sensors;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)

public class SensorNotify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "virtual_sensor_id")
    private VirtualSensor sensor;
    @Enumerated(EnumType.STRING)
    private NotifyType notifyType;
    private String message;
    @CreationTimestamp
    private LocalDateTime createdAt;


    public enum NotifyType {
        SENSOR_ERROR,
        CREATE,
        UPDATE,
        DELETE;
    }
}
