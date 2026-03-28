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
    SELECT mv.timestamp AS timestamp,
           mv.value AS value
    FROM measurement_value mv
    WHERE mv.measurement_parent_id = :id
      AND mv.timestamp < :lastTimestamp
    ORDER BY mv.timestamp DESC, mv.id DESC
    LIMIT :limit
""", nativeQuery = true)
    List<MeasurementValueView> findMeasurementValuesWithView(
            @Param("id") Long id,
            @Param("lastTimestamp") Instant lastTimestamp,
            @Param("limit") int limit
    );


    @Query(value = """
    SELECT mv.timestamp AS time,
           mv.value AS value
    FROM measurement_value mv
    WHERE mv.measurement_parent_id = :id
    AND mv.timestamp < :lastTimestamp
    ORDER BY mv.timestamp DESC, mv.id DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Object[]> findRaw(
            @Param("id") Long id,
            @Param("lastTimestamp") Instant lastTimestamp,
            @Param("limit") int limit
    );
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
