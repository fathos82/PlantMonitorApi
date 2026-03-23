package io.athos.agrocore.plantmonitor.devices;

import io.athos.agrocore.plantmonitor.users.User;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String deviceUuid;
    private String name;
    private String hostname;
    private String deviceType;   // raspberry_pi
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<VirtualSensor> sensors;
    @CreatedDate
    private LocalDateTime firstSeenAt;
    @LastModifiedDate
    private LocalDateTime lastSeenAt;

    @Transient
    public boolean isOnline() {
        return lastSeenAt != null &&
                lastSeenAt.isAfter(LocalDateTime.now().minusSeconds(60));
    }

}
