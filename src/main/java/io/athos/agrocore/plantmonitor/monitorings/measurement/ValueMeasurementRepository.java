package io.athos.agrocore.plantmonitor.monitorings.measurement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ValueMeasurementRepository extends JpaRepository<MeasurementValue,Long> {
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

    @Query(value = """
    SELECT mv.timestamp AS timestamp,
           mv.value AS value
    FROM measurement_value mv
    WHERE mv.measurement_parent_id = :id
      AND mv.timestamp >= :startTimestamp
      AND mv.timestamp <= :endTimestamp
    ORDER BY mv.timestamp DESC, mv.id DESC
    LIMIT :limit
""", nativeQuery = true)
    List<MeasurementValueView> findMeasurementValuesWithView(
            @Param("id") Long id,
            @Param("startTimestamp") Instant start,
            @Param("endTimestamp") Instant end,
            @Param("limit") int limit
    );


    @Query(value = """
    SELECT mv.timestamp AS timestamp,
           mv.value AS value
    FROM measurement_value mv
    WHERE mv.measurement_parent_id = :id
      AND mv.timestamp >= :startTimestamp
      AND mv.timestamp <= :endTimestamp
    ORDER BY mv.timestamp DESC, mv.id DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Object[]> findRaw(
            @Param("id") Long id,
            @Param("startTimestamp") Instant start,
            @Param("endTimestamp") Instant end,
            @Param("limit") int limit
    );// TODO:
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
