package io.athos.agrocore.plantmonitor.devices.sensors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorNotifyRepository extends JpaRepository<SensorNotify, Long> {
    Page<SensorNotify> findBySensorIdAndNotifyType(Long sensor_id, SensorNotify.NotifyType notifyType, Pageable pageable);

}
