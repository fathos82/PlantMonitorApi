package io.athos.agrocore.plantmonitor.devices.sensors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualSensorRepository extends JpaRepository<VirtualSensor, Long> {
    List<VirtualSensor> findAllByDevice_DeviceUuid(String deviceDeviceUuid);

    Optional<VirtualSensor> findById_AndDevice_User_Id(Long id, Long deviceUserId);

    List<VirtualSensor> findAllByDevice_DeviceUuid_AndDevice_User_Id(String deviceDeviceUuid, Long deviceUserId);

    void deleteById_AndDevice_User_Id(Long id, Long deviceUserId);
}
