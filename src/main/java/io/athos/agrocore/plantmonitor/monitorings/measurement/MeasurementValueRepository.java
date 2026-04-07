package io.athos.agrocore.plantmonitor.monitorings.measurement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MeasurementValueRepository extends JpaRepository<MeasurementValue,Long> {

    @Query(value = """
    SELECT 
        MIN(value) as min, 
        MAX(value) as max, 
        AVG(value) as avg 
    FROM measurement_value 
    WHERE measurement_parent_id = :id 
      AND "timestamp" BETWEEN :start AND :end
    """, nativeQuery = true)
    MeasurementStats findStats(@Param("id") Long id, @Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
        SELECT 
            time AS time, 
            value AS value
        FROM (
            SELECT lttb(
                time_bucket(CAST(:bucket AS interval), "timestamp"), 
                value, 
                :points 
            ) AS lttb_data
            FROM measurement_value
            WHERE measurement_parent_id = :measurementId 
              AND "timestamp" >= :startTime 
              AND "timestamp" <= :endTime
        ) sub
        """, nativeQuery = true)
    List<MeasurementValueView> findByMeasurementParentIdDownsampling(
            @Param("measurementId") Long measurementId,
            @Param("start") Instant startTime,
            @Param("end") Instant endTime,
            @Param("bucket") String bucket,
            @Param("points") Integer points
    );

    // Fallback bulk delete (if ON DELETE CASCADE is not used in DB)


    @Modifying // Obriga o Spring a pular o SELECT e mandar o DELETE direto
    @Query("DELETE FROM MeasurementValue m WHERE m.measurementParentId = :parentId")
    void deleteByMeasurementParentId(@Param("parentId") Long parentId);

}
