package io.athos.agrocore.plantmonitor.devices;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Override
    <S extends Device> S save(S entity);

    Optional<Device> getDeviceById(Long deviceId);

    List<Device> getDeviceByUser_Id(Long userId);

    Optional<Device> getDeviceByDeviceUuid(String deviceUuid);

    Optional<Device> findDeviceByIdAndUser_Id(Long id, Long userId);

    Optional<Device> getDeviceByDeviceUuid_AndUser_Id(String uuid, Long userId);
}
