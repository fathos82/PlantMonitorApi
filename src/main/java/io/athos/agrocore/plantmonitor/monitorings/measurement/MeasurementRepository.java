package io.athos.agrocore.plantmonitor.monitorings.measurement;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    Optional<Measurement> findByVirtualSensorIdAndMeasurementType(
            Long virtualSensorId,
            MeasurementType measurementType
    );
    @Modifying
    @Query("UPDATE Measurement m SET m.virtualSensor = null WHERE m.virtualSensor.id = :sensorId AND m.plantMonitoring.user.id" +
            "= :userId")
    void detachSensorFromMeasurements(@Param("sensorId") Long sensorId, @Param("userId") Long userId);
    @Query("""
        select distinct m
        from Measurement m
        left join fetch m.values v
        where m.plantMonitoring.id = :plantId
        and m.measurementType = : measurementType
        order by v.timestamp asc
    """)
    List<Measurement> findByPlantMonitoring_IdAndMeasurementTypeOrder_ValueByTimestampAsc(
            @Param("plantId") Long plantId,
            @Param("measurementType") MeasurementType measurementType
    );

    @Query("""
        select distinct m
        from Measurement m
        left join fetch m.values v
        where m.plantMonitoring.id = :plantId
        order by v.timestamp asc
    """)
    List<Measurement> findByPlantMonitoring_IdOrder_ValueByTimestampAsc(
            @Param("plantId") Long plantId);

    @Query("""
        select distinct m
        from Measurement m
        left join fetch m.values v
        where m.plantMonitoring.id = :plantId
          and (v is null or v.timestamp >= :from)
        order by v.timestamp asc
    """)
    List<Measurement> findByPlant_IdAndValuesAfter(
            @Param("plantId") Long plantId,
            @Param("from") Instant from
    );

    @Query("""
        select distinct m
        from Measurement m
        left join fetch m.values v
        where m.plantMonitoring.id = :plantId
          and m.measurementType = :measurementType
          and (v is null or v.timestamp >= :from)
        order by v.timestamp asc
    """)
    List<Measurement> findByPlant_IdAndMeasurementTypeAndValuesAfter(
            @Param("plantId") Long plantId,
            @Param("measurementType") MeasurementType measurementType,
            @Param("from") Instant from
    );

    Optional<Measurement> findByIdAndVirtualSensor_Device_User_Id(Long id, Long virtualSensorDeviceUserId);

    @Override
    void delete(Measurement entity);

    List<Measurement> findAllByPlantMonitoring_User_Id(Long id);
}
