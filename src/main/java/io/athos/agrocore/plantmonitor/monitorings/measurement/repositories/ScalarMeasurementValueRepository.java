package io.athos.agrocore.plantmonitor.monitorings.measurement.repositories;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import io.athos.agrocore.plantmonitor.monitorings.measurement.entities.ScalarMeasurementValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScalarMeasurementValueRepository extends JpaRepository<ScalarMeasurementValue,Long> {
    @Query("""
        SELECT v.value
        FROM ScalarMeasurementValue v
        WHERE v.measurementParent.plantMonitoring.id = :plantId
          AND v.measurementParent.measurementType = :type
        ORDER BY v.timestamp
    """)
    List<Double> findValues(
            @Param("plantId") Long plantId,
            @Param("type") MeasurementType type
    );
}
