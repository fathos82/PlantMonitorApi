package io.athos.agrocore.plantmonitor.monitorings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantMonitoringRepository extends JpaRepository<PlantMonitoring,Long> {
}
