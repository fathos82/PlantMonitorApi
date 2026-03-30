package io.athos.agrocore.plantmonitor.monitorings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantMonitoringRepository extends JpaRepository<PlantMonitoring,Long> {
    Optional<PlantMonitoring> findById_AndUser_Id(Long id, Long userId);

    void deleteById_AndUser_Id(Long id, Long userId);
}
