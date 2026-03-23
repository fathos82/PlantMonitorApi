package io.athos.agrocore.plantmonitor;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class MqttSubscriber {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MeasurementService measurementService;

    @Value("${mqtt.server.url:tcp://localhost:1883}")
    private String serverURL;


    @PostConstruct
    public void subscribe() throws MqttException, MqttException {

        MqttClient client =
                new MqttClient(serverURL, "plant_monitor-client");

        client.connect();
        client.subscribe("plant/data", (topic, msg) -> {
            String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(payload);
            measurementService.addValueToMeasurement(root);
        });
    }
}
