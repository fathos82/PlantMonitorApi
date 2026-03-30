package io.athos.agrocore.plantmonitor.monitorings.measurement;

import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class MeasurementValueProtoRepository {

    private final JdbcTemplate jdbc;

    private static final int TARGET_POINTS = 1000;

    private static final String BASE_QUERY = """
    SELECT mv.timestamp, mv.value
    FROM measurement_value mv
    WHERE mv.measurement_parent_id = ?
      AND mv.timestamp >= ?
      AND mv.timestamp <= ?
    ORDER BY mv.timestamp DESC
    LIMIT ?
    """;

    private static final String ADAPTIVE_QUERY = """
        SELECT time_bucket(CAST(? AS INTERVAL), mv.timestamp) AS bucket,
               AVG(mv.value)   AS value
        FROM measurement_value mv
        WHERE mv.measurement_parent_id = ?
          AND mv.timestamp BETWEEN ? AND ?
        GROUP BY bucket
        ORDER BY bucket DESC
        """;

    // equivalente ao findMeasurementValuesWithView, mas retorna Protobuf direto
    public Proto.SensorReadingsResponse findAsProto(Long id, Instant start, Instant end, int limit) {
        Proto.SensorReadingsResponse.Builder response = Proto.SensorReadingsResponse.newBuilder();

        jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(BASE_QUERY,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );
            ps.setLong(1, id);                            // measurement_parent_id = ?
            ps.setTimestamp(2, Timestamp.from(start));    // timestamp >= ?
            ps.setTimestamp(3, Timestamp.from(end));      // timestamp <= ?
            ps.setInt(4, limit);                          // LIMIT ?
            ps.setFetchSize(1000);
            return ps;
        }, rs -> {
            response.addReadings(
                    Proto.SensorReadingResponse.newBuilder()
                            .setTimestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli())
                            .setValue(rs.getFloat("value"))
                            .build()
            );
        });

        return response.build();
    }

    public Proto.SensorReadingsResponse findAdaptiveAsProto(Long id, Instant start, Instant end) {
        String bucket = resolveBucket(start, end);
        Proto.SensorReadingsResponse.Builder response = Proto.SensorReadingsResponse.newBuilder();

        jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(ADAPTIVE_QUERY,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );
            ps.setString(1, bucket);                      // CAST(? AS INTERVAL)
            ps.setLong(2, id);                            // measurement_parent_id = ?
            ps.setTimestamp(3, Timestamp.from(start));    // BETWEEN ?
            ps.setTimestamp(4, Timestamp.from(end));      // AND ?
            ps.setFetchSize(1000);
            return ps;
        }, rs -> {
            response.addReadings(
                    Proto.SensorReadingResponse.newBuilder()
                            .setTimestamp(rs.getTimestamp("bucket").toInstant().toEpochMilli())
                            .setValue(rs.getFloat("value"))
                            .build()
            );
        });

        return response.build();
    }

    private String resolveBucket(Instant from, Instant to) {
        long seconds = Duration.between(from, to).getSeconds();
        long bucketSeconds = Math.max(1, seconds / TARGET_POINTS);
        return roundToNice(bucketSeconds);
    }

    private String roundToNice(long seconds) {
        long[] nice = {
                1, 2, 5, 10, 15, 30,
                60, 120, 300, 600, 900, 1800,
                3600, 7200, 14400, 43200,
                86400, 604800
        };
        for (long n : nice) {
            if (seconds <= n) return formatBucket(n);
        }
        return "7 days";
    }

    private String formatBucket(long seconds) {
        if (seconds < 60)    return seconds + " seconds";
        if (seconds < 3600)  return (seconds / 60) + " minutes";
        if (seconds < 86400) return (seconds / 3600) + " hours";
        return                      (seconds / 86400) + " days";
    }
}