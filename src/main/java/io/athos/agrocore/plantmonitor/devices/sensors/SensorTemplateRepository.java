package io.athos.agrocore.plantmonitor.devices.sensors;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorTemplateRepository extends JpaRepository<SensorTemplate, Long> {
    boolean existsByModel(String s);
}
