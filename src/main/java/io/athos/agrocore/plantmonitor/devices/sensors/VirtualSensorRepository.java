package io.athos.agrocore.plantmonitor.devices.sensors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VirtualSensorRepository extends JpaRepository<VirtualSensor, Long> {
    List<VirtualSensor> findAllByDevice_DeviceUuid(String deviceDeviceUuid);
}
