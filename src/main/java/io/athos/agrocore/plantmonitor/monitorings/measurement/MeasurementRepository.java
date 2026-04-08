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


    Optional<Measurement> findByIdAndVirtualSensor_Device_User_Id(Long id, Long virtualSensorDeviceUserId);

    @Override
    void delete(Measurement entity);

    List<Measurement> findAllByPlantMonitoring_User_Id(Long id);
}
