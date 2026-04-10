package io.athos.agrocore.plantmonitor.configuration;

// Correção dos imports de coleções do Java
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.athos.agrocore.plantmonitor.devices.sensors.SensorTemplate;
import io.athos.agrocore.plantmonitor.devices.sensors.SensorTemplateRepository;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SensorTemplateSeeder implements ApplicationRunner {

    private final SensorTemplateRepository sensorTemplateRepository;
    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createIndex();

        seedMockSensor();
        seedHcsr04Sensor();
        seedLm35dzSensor();
        seedYl69Sensor();
        seedMq135Sensor();
    }

    private void createIndex() {
        jdbc.execute("""
            CREATE INDEX IF NOT EXISTS idx_measurement_parent_time_desc
            ON measurement_value (measurement_parent_id, timestamp DESC);
        """);
    }

    private void seedMockSensor() {
        if (sensorTemplateRepository.existsByModel("MOCK")) return;

        SensorTemplate template = new SensorTemplate();
        template.setName("Mock Sensor");
        template.setModel("MOCK");
        template.setCapabilities(new HashSet<>(Set.of(MeasurementType.MOCK)));
        template.setDefaultParameters(new HashMap<>());

        sensorTemplateRepository.save(template);
    }

    private void seedHcsr04Sensor() {
        if (sensorTemplateRepository.existsByModel("HC-SR04")) return;

        SensorTemplate template = new SensorTemplate();
        template.setName("Ultrasonic Distance Sensor");
        template.setModel("HC-SR04");
        template.setCapabilities(new HashSet<>(Set.of(MeasurementType.DISTANCE)));

        Map<String, String> params = new HashMap<>();
        params.put("trigger_pin", "23");
        params.put("echo_pin", "24");
        template.setDefaultParameters(params);

        sensorTemplateRepository.save(template);
    }

    private void seedLm35dzSensor() {
        if (sensorTemplateRepository.existsByModel("LM35DZ")) return;

        SensorTemplate template = new SensorTemplate();
        template.setName("Analog Temperature Sensor");
        template.setModel("LM35DZ");
        template.setCapabilities(new HashSet<>(Set.of(MeasurementType.TEMPERATURE)));

        Map<String, String> params = new HashMap<>();
        params.put("i2c_bus", "1");
        params.put("i2c_address", "0x48");
        params.put("adc_channel", "0");
        template.setDefaultParameters(params);

        sensorTemplateRepository.save(template);
    }

    private void seedYl69Sensor() {
        if (sensorTemplateRepository.existsByModel("YL-69")) return;

        SensorTemplate template = new SensorTemplate();
        template.setName("Soil Moisture Sensor");
        template.setModel("YL-69");
        template.setCapabilities(new HashSet<>(Set.of(MeasurementType.HUMIDITY)));

        Map<String, String> params = new HashMap<>();
        params.put("i2c_bus", "1");
        params.put("i2c_address", "0x48");
        params.put("adc_channel", "0");
        params.put("v_dry", "3.3");
        params.put("v_wet", "0.5");
        template.setDefaultParameters(params);

        sensorTemplateRepository.save(template);
    }

    private void seedMq135Sensor() {
        SensorTemplate template = sensorTemplateRepository
                .findByModel("MQ-135")
                .orElse(new SensorTemplate());

        template.setName("Air Quality Sensor");
        template.setModel("MQ-135");

        template.setCapabilities(new HashSet<>(Set.of(MeasurementType.AIR_QUALITY)));

        Map<String, String> params = new HashMap<>();
        params.put("i2c_bus", "1");
        params.put("i2c_address", "0x48");
        params.put("adc_channel", "0");
        params.put("rl_ohm", "20000");
        params.put("ro_ohm", "76000");
        params.put("calibration_gas", "CO2");

        template.setDefaultParameters(params);

        sensorTemplateRepository.save(template);
    }
}