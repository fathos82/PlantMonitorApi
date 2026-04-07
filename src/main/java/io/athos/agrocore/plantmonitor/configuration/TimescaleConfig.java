package io.athos.agrocore.plantmonitor.configuration;

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
        // Ordem importa
        jdbc.execute("CREATE EXTENSION IF NOT EXISTS timescaledb;");

        // O LTTB SÓ EXISTE SE ISSO AQUI RODAR:
        try {
            jdbc.execute("CREATE EXTENSION IF NOT EXISTS timescaledb_toolkit;");
        } catch (Exception e) {
            System.err.println("AVISO: Toolkit não encontrado. Verifique sua imagem do Docker/Postgres.");
        }

        jdbc.execute("SELECT create_hypertable('measurement_value', 'timestamp', migrate_data => true, if_not_exists => TRUE);");
    }


}