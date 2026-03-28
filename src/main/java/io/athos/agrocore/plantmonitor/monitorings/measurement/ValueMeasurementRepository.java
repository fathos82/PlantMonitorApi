package io.athos.agrocore.plantmonitor.monitorings.measurement;

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


    @Query(value = """
    SELECT (extract(epoch FROM timestamp) * 1000) AS time,
           value AS value
    FROM measurement_value
    WHERE measurement_parent_id = :id
      AND timestamp < :lastTimestamp
    ORDER BY timestamp DESC
    LIMIT :limit
""", nativeQuery = true)
    List<MeasurementValueView> findMeasurementValue(
            Long id,
            Instant lastTimestamp,
            int limit
    );

    @Query(value = """
    SELECT extract(epoch FROM timestamp) * 1000 AS time,
           value
    FROM measurement_value
    WHERE measurement_parent_id = :id
    ORDER BY timestamp DESC
""", nativeQuery = true)
    List<Object[]> findRaw(Long id);

// TODO:
//@GetMapping
//public void stream(HttpServletResponse response) throws IOException {
//    var writer = response.getWriter();
//
//    writer.write("[");
//
//    jdbcTemplate.query("SELECT ...", rs -> {
//        writer.write("[" + rs.getLong(1) + "," + rs.getDouble(2) + "],");
//    });
//
//    writer.write("]");
//}
}
