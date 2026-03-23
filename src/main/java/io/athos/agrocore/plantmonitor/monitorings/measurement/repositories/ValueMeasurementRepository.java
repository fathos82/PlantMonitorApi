package io.athos.agrocore.plantmonitor.monitorings.measurement.repositories;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import io.athos.agrocore.plantmonitor.monitorings.measurement.entities.MeasurementValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ValueMeasurementRepository extends JpaRepository<MeasurementValue,Long> {
    boolean existsByIdAfter(Long idAfter);

    boolean existsByTimestampAfter(Instant timestampAfter);


    List<MeasurementValue> findAllByMeasurementParent_PlantMonitoring_IdAndMeasurementParent_MeasurementTypeAndTimestampAfter(Long measurementParent_plantMonitoring_id, MeasurementType measurementParent_measurementType, Instant timestamp);
    List<MeasurementValue> findAllByMeasurementParent_PlantMonitoring_IdAndMeasurementParent_MeasurementType(Long measurementParent_plantMonitoring_id, MeasurementType measurementParent_measurementType);


    @Query("""
        SELECT MAX(v.timestamp)
        FROM MeasurementValue v
        WHERE v.measurementParent.plantMonitoring.id = :plantId
          AND v.measurementParent.measurementType = :type
    """)
    Instant findLastModified(
            @Param("plantId") Long plantId,
            @Param("type") MeasurementType type
    );

    List<MeasurementValue> findAllByMeasurementParent_PlantMonitoring_IdAndTimestampAfter(Long plantId, Instant instant);
}
