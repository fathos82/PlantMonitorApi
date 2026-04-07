import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TimescaleConfig {

    private final JdbcTemplate jdbc;

    public TimescaleConfig(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("CREATE EXTENSION IF NOT EXISTS timescaledb;");
        jdbc.execute("SELECT create_hypertable('medicao', 'timestamp', if_not_exists => TRUE);");
    }
}