package io.athos.agrocore.plantmonitor.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbc;

    public DatabaseInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {

        // 🔥 Criar índice
        jdbc.execute("""
            CREATE INDEX IF NOT EXISTS idx_measurement_parent_time_desc
            ON measurement_value (measurement_parent_id, timestamp DESC);
        """);
    }
}