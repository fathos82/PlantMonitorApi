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
        point.time AS time, 
        point.value AS value
    FROM (
        -- O LTTB atua direto na massa de dados brutos
        SELECT lttb(
            "timestamp", 
            value, 
            :points 
        ) AS lttb_data
        FROM measurement_value
        WHERE measurement_parent_id = :measurementId 
          AND "timestamp" >= :start 
          AND "timestamp" <= :end
    ) sub
    -- Usamos o LATERAL JOIN para desempacotar com o máximo de performance
    CROSS JOIN LATERAL public.unnest(sub.lttb_data) AS point
    """, nativeQuery = true)
    List<MeasurementValueView> findByMeasurementParentIdDownsampling(
            @Param("measurementId") Long measurementId,
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("points") Integer points
            // REMOVI O PARAMETRO 'bucket' DAQUI. NÃO PRECISA MAIS!
    );

    @Modifying // Obriga o Spring a pular o SELECT e mandar o DELETE direto
    @Query("DELETE FROM MeasurementValue m WHERE m.measurementParentId = :parentId")
    void deleteByMeasurementParentId(@Param("parentId") Long parentId);

}
