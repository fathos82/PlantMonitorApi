package io.athos.agrocore.plantmonitor.devices.sensors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorTemplateRepository extends JpaRepository<SensorTemplate, Long> {
    boolean existsByModel(String s);



    Optional<SensorTemplate> findByModel(String s);
}
