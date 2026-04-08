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
        try {
            // 1. Ativa as extensões (Core e Toolkit para o LTTB)
            jdbc.execute("CREATE EXTENSION IF NOT EXISTS timescaledb;");

            try {
                jdbc.execute("CREATE EXTENSION IF NOT EXISTS timescaledb_toolkit;");
            } catch (Exception e) {
                System.err.println("⚠️ TimescaleDB Toolkit não disponível. O gráfico LTTB pode falhar.");
            }



            // 3. Verifica se a tabela já existe antes de tentar converter em Hypertable
            // O Hibernate pode demorar uns milissegundos para criar a tabela
            boolean tableExists = Boolean.TRUE.equals(jdbc.queryForObject(
                    "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'measurement_value')",
                    boolean.class
            ));

            if (tableExists) {

                jdbc.execute("SELECT create_hypertable('measurement_value', 'timestamp', " +
                        "migrate_data => true, if_not_exists => TRUE);");
                System.out.println(" Hypertable 'measurement_value' verificada/criada.");
            } else {
                System.err.println(" Tabela 'measurement_value' ainda não foi criada pelo Hibernate. " +
                        "A conversão ocorrerá na próxima inicialização.");
            }

        } catch (Exception e) {
            // ESSENCIAL: Loga o erro mas NÃO deixa a exceção subir.
            // Se a exceção subir, o Spring mata o TransactionManager e o app não liga.
            System.err.println(" Erro crítico na configuração do TimescaleDB: " + e.getMessage());
        }
    }


}