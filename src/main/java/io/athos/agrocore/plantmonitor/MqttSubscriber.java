package io.athos.agrocore.plantmonitor;

import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementService;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// IMPORTANTE: Importe a classe que o compilador do Protobuf gerou para você
import io.athos.agrocore.plantmonitor.devices.sensors.Proto.SensorReadingBatch;
import com.google.protobuf.InvalidProtocolBufferException;

@Component
public class MqttSubscriber {

    // Removemos o ObjectMapper, o Protobuf é autossuficiente!

    @Autowired
    MeasurementService measurementService;

    @Value("${mqtt.server.url:tcp://147.93.176.117:1883}")
    private String serverURL;

    @PostConstruct
    public void subscribe() throws MqttException {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        // Dica: Para resiliência em produção, ative a reconexão automática
        options.setAutomaticReconnect(true);

        MqttClient client = new MqttClient(serverURL, MqttClient.generateClientId());
        client.connect(options);


        client.subscribe("plant_monitor/#", (topic, msg) -> {

            try {
                // TODO: Add Device UuID
                String[] parts = topic.split("/");
                if (parts.length != 3) return;

                Long sensorId = Long.parseLong(parts[1]);
                String capability = parts[2];

                MeasurementType measurementType = MeasurementType.fromString(capability);


                // 2. PROTOBUF: Pegamos os bytes puros da rede (sem converter para String!)
                byte[] payloadBinario = msg.getPayload();

                // Desempacota os bytes diretamente para o Objeto Java gerado pelo .proto
                SensorReadingBatch batch = SensorReadingBatch.parseFrom(payloadBinario);
                System.out.println(SensorReadingBatch.getDefaultInstance().toByteString());
                measurementService.saveFromBatch(measurementType,sensorId,batch);


            } catch (InvalidProtocolBufferException e) {
                // Cai aqui se algum sensor antigo mandar JSON ou lixo em vez de Protobuf
                System.err.println("Ignorando payload inválido no tópico " + topic + ". Não é um Protobuf válido.");
            }
            catch (NotFoundException ignore){

            }
            catch (Exception e) {
                System.err.println("Erro ao processar leitura: " + e.getMessage());
            }

        });
    }
}